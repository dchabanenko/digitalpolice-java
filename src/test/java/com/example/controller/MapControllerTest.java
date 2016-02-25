package com.example.controller;

import com.example.CityPoliceApplication;
import com.example.dto.CrimeListDTO;
import com.example.service.MapService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.sql.Date;
import java.util.List;
import java.util.Scanner;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = CityPoliceApplication.class)
@WebAppConfiguration
public class MapControllerTest {

    @Mock
    private MapService mapService;

    @Mock
    private ConversionService conversionService;

    @InjectMocks
    private MapController mapController;

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(mapController).build();
    }

    @Test
    public void testGetUnfilteredCrimesList() throws Exception {
        when(conversionService.convert(any(List.class), any(Class.class))).thenReturn(createTwoCrimesDTO());

        mockMvc.perform(get("/map"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedUnfilteredCrimeListJSON(), true));
    }

    private String expectedUnfilteredCrimeListJSON() {
        return readFromResource("com.example.controller/crimes-list.json");
    }

    private String readFromResource(String resourceName) {
        try {
            return new Scanner(new ClassPathResource(resourceName).getInputStream()).useDelimiter("\\A").next();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private CrimeListDTO createTwoCrimesDTO() {
        CrimeListDTO dtoList = new CrimeListDTO();

        CrimeListDTO.CrimeDTO dto = new CrimeListDTO().new CrimeDTO();
        dto.setCoordinates(-111.538593, 37.000674);
        dto.setId(123l);
        dto.setDate(new Date(1456209116l));
        dto.setCategoryId(8l);
        dto.setRegionId(7l);
        dtoList.addFeature(dto);

        CrimeListDTO.CrimeDTO dtoTwo = new CrimeListDTO().new CrimeDTO();
        dtoTwo.setCoordinates(-123.538593, 40.000674);
        dtoTwo.setId(456l);
        dtoTwo.setDate(new Date(1456209117l));
        dtoTwo.setCategoryId(3l);
        dtoTwo.setRegionId(4l);
        dtoList.addFeature(dtoTwo);

        return dtoList;
    }
}