package com.multiplos.cuentas.services.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.multiplos.cuentas.pojo.persona.PersonaResponse;
import com.multiplos.cuentas.services.GeneraPdfService;
import com.multiplos.cuentas.services.PersonaService;

@Component
public class GeneraPdfServiceImpl implements GeneraPdfService {

	private static final Logger LOG = LoggerFactory.getLogger(GeneraPdfServiceImpl.class);
	private PersonaService personaService;
	
	@Autowired
	public GeneraPdfServiceImpl(PersonaService personaService) {
		this.personaService = personaService;
	}

	@Override
	public ByteArrayOutputStream getLicitudFondoPDF(String identificacion)throws Exception {
		PersonaResponse persona = new PersonaResponse();
		String nombresCompletos;
        try{
            Document document = new Document(PageSize.A4,60,60,50,50);
            //Creamos la instancia de memoria en la que se escribirá el archivo temporalmente
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
          
            persona = personaService.consultaDatosPersona(identificacion);
                     
            LocalDate fecha;
            Font fuente = new Font();
            fuente.setFamily(BaseFont.TIMES_ROMAN.toString());
            fuente.setSize(12);

            Font fuenteNegrita = new Font();
            fuenteNegrita.setFamily(BaseFont.TIMES_ROMAN.toString());
            fuenteNegrita.setSize(12);
            fuenteNegrita.setStyle(Font.BOLD);

            Image imagen = Image.getInstance("multiplo.png");
            imagen.setAlignment(Element.ALIGN_CENTER);
            imagen.scalePercent(10f);
            
            fecha = LocalDate.now();
            Paragraph fechaDoc = new Paragraph("Guayaquil, "+fecha.getDayOfMonth()+" de "+fecha.getMonth().getDisplayName(TextStyle.SHORT,Locale.getDefault()) +" del "+fecha.getYear(),fuenteNegrita);
            fechaDoc.setAlignment(Paragraph.ALIGN_LEFT);
            fechaDoc.setSpacingAfter(20);
            
            Paragraph tituloDoc = new Paragraph("Declaración de Licitud de Fondos (PARA INVERSIONISTAS)",fuenteNegrita);
            tituloDoc.setAlignment(Paragraph.ALIGN_CENTER);
            tituloDoc.setSpacingAfter(15);
            
            nombresCompletos = persona.getNombres()+" "+persona.getApellidos();
            
            //Paragraph parrafo = new Paragraph("Yo, NOMBRESCOMPLETOS portador de la cédula de ciudadanía No. NUMCEDULA por mis propios derechos, manifiesto que la información descrita en el formulario de usuario en calidad de inversionista para el financiamiento de los proyectos utilizando la plataforma virtual de MULTIPLOS S.A.S. B.I.C. es válida, y realizo la siguiente declaración de licitud de origen de fondos y actividades lícitas:",fuente);
            Paragraph parrafo = new Paragraph("Yo, "+nombresCompletos+" portador de la cédula de ciudadanía No. "+persona.getIdentificacion()+" por mis propios derechos, manifiesto que la información descrita en el formulario de usuario en calidad de inversionista para el financiamiento de los proyectos utilizando la plataforma virtual de MULTIPLOS S.A.S. B.I.C. es válida, y realizo la siguiente declaración de licitud de origen de fondos y actividades lícitas:",fuente);
            parrafo.setAlignment(Paragraph.ALIGN_JUSTIFIED);
            parrafo.setSpacingAfter(15);
            
            Paragraph parrafo2 = new Paragraph("1. Declaro que las actividades generadoras de fondos que invierto en el/los proyectos publicados en la plataforma virtual de MULTIPLOS S.A.S. B.I.C., son actividades consideradas lícitas en conformidad con la normativa ecuatoriana y/o provienen de mi salario percibido en relación de dependencia con mi empleador y/o actividades comerciales autónomas, así como ahorros personales, herencias, ventas de activos y/ o donaciones.",fuente);
            parrafo2.setAlignment(Paragraph.ALIGN_JUSTIFIED);
            parrafo2.setSpacingAfter(15);
            
            Paragraph parrafo3 = new Paragraph("2. Que no me encuentro en ninguna lista de reporte criminal internacional ni participo en actividades de narcotráfico, lavado de activos, o delitos asociados al turismo sexual en menores de edad.",fuente);
            parrafo3.setAlignment(Paragraph.ALIGN_JUSTIFIED);
            parrafo3.setSpacingAfter(15);
            
            Paragraph parrafo4 = new Paragraph("3. Que en mi contra no existe ningún proceso judicial ni nacional ni internacional, por ninguna de las mencionadas causas anteriores.",fuente);
            parrafo4.setAlignment(Paragraph.ALIGN_JUSTIFIED);
            parrafo4.setSpacingAfter(15);
            
            Paragraph parrafo5 = new Paragraph("4. Eximo a MULTIPLOS S.A.S. B.I.C. de toda responsabilidad que se derive por información errónea, falsa o inexacta que yo hubiere proporcionado en este u otro documento durante el proceso de inversión en el/los proyectos utilizando la plataforma virtual de https://multiplolenders.com",fuente);
            parrafo5.setAlignment(Paragraph.ALIGN_JUSTIFIED);
            parrafo5.setSpacingAfter(15);
            
            Paragraph parrafo6 = new Paragraph("Declaro (amos) haber leído, entendido y aceptado libremente el presente documento.",fuente);
            parrafo6.setAlignment(Paragraph.ALIGN_JUSTIFIED);
            parrafo6.setSpacingAfter(15);
                        
            PdfPTable tabla = new PdfPTable(1);
            tabla.setWidthPercentage(70f);
            
            PdfPCell celda0 = new PdfPCell(new Phrase("Firma",fuenteNegrita));
            celda0.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
            celda0.setMinimumHeight(60f);            
            
            PdfPTable tabla2 = new PdfPTable(1);
            PdfPCell celda1 = new PdfPCell(new Phrase("Nombres Completos: "+nombresCompletos,fuente));
            celda1.setBorder(0);
            celda1.setMinimumHeight(30f);
            PdfPCell celda2 = new PdfPCell(new Phrase("C.I. o Pasaporte: "+persona.getIdentificacion(),fuente));
            celda2.setBorder(0);
            celda2.setMinimumHeight(30f);
            
            tabla2.addCell(celda1);
            tabla2.addCell(celda2);
            
            tabla.addCell(celda0);
            tabla.addCell(tabla2);

            // Asignamos la variable ByteArrayOutputStream bos donde se escribirá el documento
            PdfWriter.getInstance(document, baos);
            document.open();
            
            document.add(imagen);
            
            //document.add(fechaDoc);
            document.add(tituloDoc);
            document.add(parrafo);
            document.add(parrafo2);
            document.add(parrafo3);
            document.add(parrafo4);
            document.add(parrafo5);
            document.add(parrafo6);
            
            document.add(tabla);
            document.close();
            
            // Retornamos la variable al finalizar
            return baos;
        } catch (DocumentException e) {
        	LOG.error("Error "+e.getMessage());
            
        } catch (IOException e) {
        	LOG.error("Error "+e.getMessage());
		}
        return null;
    }
}
