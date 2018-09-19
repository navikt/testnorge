import { Kategorier, SubKategorier } from '../Categories'
import { Attributt, InputType, DataSource } from '../Types'
import Formatters from '~/utils/DataFormatter'

import * as yup from 'yup'

const AttributtListe: Attributt[] = [
	{
		hovedKategori: Kategorier.Adresser,
		subKategori: SubKategorier.Boadresse,
		id: 'boadresse.gateadresse',
		label: 'Gatenavn',
		dataSource: DataSource.TPSF,
		inputType: InputType.Text,
		validation: yup.string(),
		kanRedigeres: true
	},
	{
		hovedKategori: Kategorier.Adresser,
		subKategori: SubKategorier.Boadresse,
		id: 'boadresse.husnummer',
		label: 'Husnummer',
		dataSource: DataSource.TPSF,
		inputType: InputType.Text,
		validation: yup.string(),
		kanRedigeres: true
	},
	{
		hovedKategori: Kategorier.Adresser,
		subKategori: SubKategorier.Boadresse,
		id: 'boadresse.postnr',
		label: 'Postnummer',
		dataSource: DataSource.TPSF,
		inputType: InputType.Text,
		validation: yup.string(),
		kanRedigeres: true
	},
	{
		hovedKategori: Kategorier.Adresser,
		subKategori: SubKategorier.Boadresse,
		id: 'boadresse.kommunenr',
		label: 'Kommunenummer',
		dataSource: DataSource.TPSF,
		inputType: InputType.Text,
		validation: yup.string(),
		kanRedigeres: true
	},
	// {
	// 	hovedKategori: Kategorier.Adresser,
	// 	subKategori: SubKategorier.Boadresse,
	// 	id: 'geoTilknytning',
	// 	label: 'Geografisk tilknytning',
	// 	dataSource: DataSource.TPSF,
	// 	inputType: InputType.Text,
	// 	validation: yup.string()
	// },
	{
		hovedKategori: Kategorier.Adresser,
		subKategori: SubKategorier.Boadresse,
		id: 'boadresse.flyttedato',
		label: 'Flyttedato',
		dataSource: DataSource.TPSF,
		inputType: InputType.Date,
		validation: yup.date(),
		format: Formatters.formatDate,
		kanRedigeres: true
	}
	// {
	// 	hovedKategori: Kategorier.Adresser,
	// 	subKategori: SubKategorier.Boadresse,
	// 	id: 'matrikkeladresse',
	// 	label: 'Matrikkeladresse',
	// 	dataSource: DataSource.TPSF,
	// 	inputType: InputType.Text,
	// 	validation: yup.string()
	// },
	// {
	// 	hovedKategori: Kategorier.Adresser,
	// 	subKategori: SubKategorier.Boadresse,
	// 	id: 'gardsnummer',
	// 	label: 'Gårdsnummer',
	// 	dataSource: DataSource.TPSF,
	// 	inputType: InputType.Text,
	// 	validation: yup.string()
	// },
	// {
	// 	hovedKategori: Kategorier.Adresser,
	// 	subKategori: SubKategorier.Boadresse,
	// 	id: 'bruksnummer',
	// 	label: 'Bruksnummer',
	// 	dataSource: DataSource.TPSF,
	// 	inputType: InputType.Text,
	// 	validation: yup.string()
	// }
	// {
	// 	hovedKategori: Kategorier.Adresser,
	// 	subKategori: SubKategorier.PostadrInnland,
	// 	id: 'gatenavn',
	// 	label: 'Gatenavn',
	// 	dataSource: DataSource.TPSF,
	// 	inputType: InputType.Text,
	// 	validation: yup.string()
	// },
	// {
	// 	hovedKategori: Kategorier.Adresser,
	// 	subKategori: SubKategorier.PostadrInnland,
	// 	id: 'husnummer',
	// 	label: 'Husnummer',
	// 	dataSource: DataSource.TPSF,
	// 	inputType: InputType.Text,
	// 	validation: yup.string()
	// },
	// {
	// 	hovedKategori: Kategorier.Adresser,
	// 	subKategori: SubKategorier.PostadrInnland,
	// 	id: 'postnummer',
	// 	label: 'Postnummer',
	// 	dataSource: DataSource.TPSF,
	// 	inputType: InputType.Text,
	// 	validation: yup.string()
	// },
	// {
	// 	hovedKategori: Kategorier.Adresser,
	// 	subKategori: SubKategorier.PostadrInnland,
	// 	id: 'geoTilknytning',
	// 	label: 'Geografisk tilknytning',
	// 	dataSource: DataSource.TPSF,
	// 	inputType: InputType.Text,
	// 	validation: yup.string()
	// },
	// {
	// 	hovedKategori: Kategorier.Adresser,
	// 	subKategori: SubKategorier.PostadrInnland,
	// 	id: 'flyttedato',
	// 	label: 'Flyttedato',
	// 	dataSource: DataSource.TPSF,
	// 	inputType: InputType.Date,
	// 	validation: yup.date()
	// },
	// {
	// 	hovedKategori: Kategorier.Adresser,
	// 	subKategori: SubKategorier.PostadrUtland,
	// 	id: 'gatenavn',
	// 	label: 'Gatenavn',
	// 	dataSource: DataSource.TPSF,
	// 	inputType: InputType.Text,
	// 	validation: yup.string()
	// },
	// {
	// 	hovedKategori: Kategorier.Adresser,
	// 	subKategori: SubKategorier.PostadrUtland,
	// 	id: 'husnummer',
	// 	label: 'Husnummer',
	// 	dataSource: DataSource.TPSF,
	// 	inputType: InputType.Text,
	// 	validation: yup.string()
	// },
	// {
	// 	hovedKategori: Kategorier.Adresser,
	// 	subKategori: SubKategorier.PostadrUtland,
	// 	id: 'postnummer',
	// 	label: 'Postnummer',
	// 	dataSource: DataSource.TPSF,
	// 	inputType: InputType.Text,
	// 	validation: yup.string()
	// },
	// {
	// 	hovedKategori: Kategorier.Adresser,
	// 	subKategori: SubKategorier.PostadrUtland,
	// 	id: 'geoTilknytning',
	// 	label: 'Geografisk tilknytning',
	// 	dataSource: DataSource.TPSF,
	// 	inputType: InputType.Text,
	// 	validation: yup.string()
	// },
	// {
	// 	hovedKategori: Kategorier.Adresser,
	// 	subKategori: SubKategorier.PostadrUtland,
	// 	id: 'flyttedato',
	// 	label: 'Flyttedato',
	// 	dataSource: DataSource.TPSF,
	// 	inputType: InputType.Date,
	// 	validation: yup.date()
	// }
]

export default AttributtListe
