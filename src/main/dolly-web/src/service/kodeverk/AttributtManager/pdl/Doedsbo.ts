import { Kategorier, SubKategorier, SubSubKategorier } from '../Categories'
import { Attributt, InputType, DataSource, AttributtType } from '../Types'
import SelectOptionsManager from '~/service/kodeverk/SelectOptionsManager/SelectOptionsManager'
import DateValidation from '~/components/fields/Datepicker/DateValidation'

import * as yup from 'yup'

// const AttributtListe: Attributt[] = [
// 	{
// 		hovedKategori: Kategorier.PersInfo,
// 		subKategori: SubKategorier.Doedsbo,
// 		id: 'doedsbo',
// 		label: 'Kontaktinformasjon for dødsbo',
// 		subGruppe: 'true',
// 		dataSource: DataSource.KRR,
// 		attributtType: AttributtType.SelectAndEdit,
// 		validation: yup.object(),
// 		items: [
// 			{
// 				hovedKategori: Kategorier.PersInfo,
// 				subKategori: SubKategorier.Adressat,
// 				id: 'adressatType',
// 				label: 'Adressattype',
// 				size: 'medium',
// 				subGruppe: 'Adressat',
// 				dataSource: DataSource.AAREG,
// 				validation: yup.string().required('Velg en type adressat'),
// 				inputType: InputType.Select,
// 				options: SelectOptionsManager('adressattype'),
// 				attributtType: AttributtType.SelectAndRead
// 			},
// 			{
// 				hovedKategori: Kategorier.PersInfo,
// 				subKategori: SubKategorier.Adressat,
// 				id: 'identnummer',
// 				label: 'Identnummer',
// 				subGruppe: 'Adressat',
// 				dataSource: DataSource.AAREG,
// 				validation: yup
// 					.string()
// 					.matches(/^[0-9]*$/, 'Id-nummer må være et tall med 11 sifre')
// 					.test('len', 'Id-nummer må være et tall med 11 sifre', val => val && val.length === 11),
// 				inputType: InputType.Text,
// 				onlyShowAfterSelectedValue: { attributtId: 'adressatType', valueIndex: 2 },
// 				attributtType: AttributtType.SelectAndEdit
// 			},
// 			{
// 				hovedKategori: Kategorier.PersInfo,
// 				subKategori: SubKategorier.Krr,
// 				id: 'foedselsdato',
// 				label: 'Fødselsdato',
// 				subGruppe: 'Adressat',
// 				dataSource: DataSource.KRR,
// 				validation: DateValidation(false),
// 				inputType: InputType.Date,
// 				onlyShowAfterSelectedValue: { attributtId: 'adressatType', valueIndex: 3 },
// 				attributtType: AttributtType.SelectAndEdit
// 			},
// 			{
// 				hovedKategori: Kategorier.PersInfo,
// 				subKategori: SubKategorier.Krr,
// 				id: 'fornavn',
// 				label: 'Fornavn',
// 				subGruppe: 'Adressat',
// 				dataSource: DataSource.KRR,
// 				inputType: InputType.Text,
// 				validation: yup.string().required('Vennligst fyll inn navn'),
// 				attributtType: AttributtType.SelectAndEdit
// 			},
// 			{
// 				hovedKategori: Kategorier.PersInfo,
// 				subKategori: SubKategorier.Krr,
// 				id: 'mellomnavn',
// 				label: 'mellomnavn',
// 				subGruppe: 'Adressat',
// 				dataSource: DataSource.KRR,
// 				inputType: InputType.Text,
// 				attributtType: AttributtType.SelectAndEdit
// 			},
// 			{
// 				hovedKategori: Kategorier.PersInfo,
// 				subKategori: SubKategorier.Krr,
// 				id: 'etternavn',
// 				label: 'Etternavn',
// 				subGruppe: 'Adressat',
// 				dataSource: DataSource.KRR,
// 				inputType: InputType.Text,
// 				validation: yup.string().required('Vennligst fyll inn navn'),
// 				attributtType: AttributtType.SelectAndEdit
// 			},
// 			{
// 				hovedKategori: Kategorier.PersInfo,
// 				subKategori: SubKategorier.Krr,
// 				id: 'org_orgnavn',
// 				label: 'Organisasjonsnavn',
// 				subGruppe: 'Adressat',
// 				dataSource: DataSource.KRR,
// 				onlyShowAfterSelectedValue: { attributtId: 'adressatType', valueIndex: 1 },
// 				inputType: InputType.Text,
// 				attributtType: AttributtType.SelectAndEdit,
// 				validation: yup.string().required('Vennligst fyll inn organisasjonsnavn')
// 			},
// 			{
// 				hovedKategori: Kategorier.PersInfo,
// 				subKategori: SubKategorier.Krr,
// 				id: 'advokat_orgnavn',
// 				label: 'Organisasjonsnavn',
// 				subGruppe: 'Adressat',
// 				dataSource: DataSource.KRR,
// 				onlyShowAfterSelectedValue: { attributtId: 'adressatType', valueIndex: 0 },
// 				inputType: InputType.Text,
// 				attributtType: AttributtType.SelectAndEdit
// 			},
// 			{
// 				hovedKategori: Kategorier.PersInfo,
// 				subKategori: SubKategorier.Arbeidsforhold,
// 				id: 'org_orgnr',
// 				label: 'Organisasjonsnummer',
// 				subGruppe: 'Adressat',
// 				dataSource: DataSource.AAREG,
// 				inputType: InputType.Text,
// 				onlyShowAfterSelectedValue: { attributtId: 'adressatType', valueIndex: 1 },
// 				// Egen validation pga yup tror stor streng ikke er integer
// 				validation: yup
// 					.string()
// 					.matches(/^[0-9]*$/, 'Orgnummer må være et tall med 9 sifre')
// 					.test('len', 'Orgnummer må være et tall med 9 sifre', val => val && val.length === 9),
// 				attributtType: AttributtType.SelectAndRead
// 			},
// 			{
// 				hovedKategori: Kategorier.PersInfo,
// 				subKategori: SubKategorier.Arbeidsforhold,
// 				id: 'advokat_orgnr',
// 				label: 'Organisasjonsnummer',
// 				subGruppe: 'Adressat',
// 				dataSource: DataSource.AAREG,
// 				inputType: InputType.Text,
// 				onlyShowAfterSelectedValue: { attributtId: 'adressatType', valueIndex: 0 },
// 				// Egen validation pga yup tror stor streng ikke er integer
// 				validation: yup
// 					.string()
// 					.matches(/^[0-9]*$/, 'Orgnummer må være et tall med 9 sifre')
// 					.test('len', 'Orgnummer må være et tall med 9 sifre', val => val && val.length === 9),
// 				attributtType: AttributtType.SelectAndRead
// 			},
// 			{
// 				hovedKategori: Kategorier.PersInfo,
// 				subKategori: SubKategorier.Adresse,
// 				id: 'land',
// 				parent: 'adresse',
// 				label: 'Land',
// 				subGruppe: 'Adresse',
// 				dataSource: DataSource.TPSF,
// 				inputType: InputType.Select,
// 				apiKodeverkId: 'Landkoder',
// 				validation: yup.string().required('Vennligst fyll inn land'),
// 				defaultValue: 'NOR',
// 				attributtType: AttributtType.SelectAndRead
// 			},
// 			{
// 				hovedKategori: Kategorier.PersInfo,
// 				subKategori: SubKategorier.Adresse,
// 				id: 'adresselinje1',
// 				path: 'adresse.adresselinje1',
// 				parent: 'adresse',
// 				label: 'Adresselinje 1',
// 				subGruppe: 'Adresse',
// 				dataSource: DataSource.TPSF,
// 				inputType: InputType.Text,
// 				validation: yup.string().required('Vennligst fyll ut'),
// 				attributtType: AttributtType.SelectAndRead
// 			},
// 			{
// 				hovedKategori: Kategorier.PersInfo,
// 				subKategori: SubKategorier.Adresse,
// 				id: 'adresselinje2',
// 				path: 'adresse.adresselinje2',
// 				parent: 'adresse',
// 				label: 'Adresselinje 2',
// 				subGruppe: 'Adresse',
// 				dataSource: DataSource.TPSF,
// 				inputType: InputType.Text,
// 				attributtType: AttributtType.SelectAndRead
// 			},
// 			{
// 				hovedKategori: Kategorier.PersInfo,
// 				subKategori: SubKategorier.Adresse,
// 				id: 'postnummer',
// 				path: 'adresse.postnummer',
// 				parent: 'adresse',
// 				label: 'Postnummer og -sted',
// 				subGruppe: 'Adresse',
// 				dataSource: DataSource.TPSF, //endres
// 				apiKodeverkShowValueInLabel: true,
// 				inputType: InputType.Select,
// 				apiKodeverkId: 'Postnummer',
// 				validation: yup.string().required('Vennligst fyll inn postnummer/-sted'),
// 				attributtType: AttributtType.SelectAndEdit
// 			},
// 			{
// 				hovedKategori: Kategorier.PersInfo,
// 				subKategori: SubKategorier.Adresse,
// 				id: 'utenlandsk_postnummer',
// 				path: 'adresse.utenlandsk_postnummer',
// 				parent: 'adresse',
// 				label: 'Postnummer',
// 				subGruppe: 'Adresse',
// 				dataSource: DataSource.TPSF, //endres
// 				inputType: InputType.Text,
// 				validation: yup
// 					.string()
// 					.matches(/^[0-9]*$/, 'Postnummeret må være et tall med 4 sifre')
// 					.test('len', 'Postnummeret må være et tall med 4 sifre', val => val && val.length === 4),
// 				attributtType: AttributtType.SelectAndEdit
// 			},
// 			{
// 				hovedKategori: Kategorier.PersInfo,
// 				subKategori: SubKategorier.Adresse,
// 				id: 'utenlandsk_poststed',
// 				path: 'adresse.utenlandsk_poststed',
// 				parent: 'adresse',
// 				label: 'Poststed',
// 				subGruppe: 'Adresse',
// 				dataSource: DataSource.TPSF,
// 				validation: yup.string().required('Vennligst fyll inn poststed'),
// 				inputType: InputType.Text,
// 				attributtType: AttributtType.SelectAndRead
// 			},
// 			{
// 				hovedKategori: Kategorier.PersInfo,
// 				subKategori: SubKategorier.Skifte,
// 				id: 'skifteform',
// 				label: 'Skifteform',
// 				subGruppe: 'Annet',
// 				dataSource: DataSource.KRR,
// 				inputType: InputType.Select,
// 				validation: yup.string().required('Vennligst velg'),
// 				options: SelectOptionsManager('skifteform'),
// 				attributtType: AttributtType.SelectAndEdit
// 			},
// 			{
// 				hovedKategori: Kategorier.PersInfo,
// 				subKategori: SubKategorier.Skifte,
// 				id: 'utstedtDato',
// 				label: 'Skifteform utstedt',
// 				subGruppe: 'Annet',
// 				dataSource: DataSource.KRR,
// 				inputType: InputType.Date,
// 				validation: DateValidation(),
// 				attributtType: AttributtType.SelectAndEdit,
// 				defaultValue: new Date()
// 			},
// 			{
// 				hovedKategori: Kategorier.PersInfo,
// 				subKategori: SubKategorier.Krr,
// 				id: 'gyldigFra',
// 				label: 'Gyldig fra',
// 				subGruppe: 'Annet',
// 				dataSource: DataSource.KRR,
// 				validation: DateValidation(),
// 				inputType: InputType.Date,
// 				attributtType: AttributtType.SelectAndEdit,
// 				defaultValue: new Date()
// 			},
// 			{
// 				hovedKategori: Kategorier.PersInfo,
// 				subKategori: SubKategorier.Krr,
// 				id: 'gyldigTil',
// 				label: 'Gyldig til',
// 				subGruppe: 'Annet',
// 				dataSource: DataSource.KRR,
// 				validation: DateValidation(false),
// 				inputType: InputType.Date,
// 				attributtType: AttributtType.SelectAndEdit
// 			}
// 		]
// 	}
// ]
const AttributtListe: Attributt[] = [
	{
		hovedKategori: Kategorier.Doedsbo,
		subKategori: SubKategorier.Doedsbo,
		id: 'doedsbo',
		label: 'Kontaktinformasjon for dødsbo',
		dataSource: DataSource.KRR,
		subGruppe: 'Doedsbo',
		attributtType: AttributtType.SelectAndEdit,
		validation: yup.object(),
		items: [
			{
				hovedKategori: Kategorier.Doedsbo,
				subKategori: SubKategorier.Adressat,
				id: 'adressatType',
				label: 'Adressattype',
				size: 'medium',
				subGruppe: 'Adressat',
				dataSource: DataSource.AAREG,
				validation: yup.string().required('Velg en type adressat'),
				inputType: InputType.Select,
				options: SelectOptionsManager('adressattype'),
				attributtType: AttributtType.SelectAndRead
			},
			{
				hovedKategori: Kategorier.Doedsbo,
				subKategori: SubKategorier.Adressat,
				id: 'identnummer',
				label: 'Identnummer',
				subGruppe: 'Adressat',
				dataSource: DataSource.AAREG,
				validation: yup
					.string()
					.matches(/^[0-9]*$/, 'Id-nummer må være et tall med 11 sifre')
					.test('len', 'Id-nummer må være et tall med 11 sifre', val => val && val.length === 11),
				inputType: InputType.Text,
				onlyShowAfterSelectedValue: { attributtId: 'adressatType', valueIndex: 2 },
				attributtType: AttributtType.SelectAndEdit
			},
			{
				hovedKategori: Kategorier.Doedsbo,
				subKategori: SubKategorier.Adressat,
				id: 'foedselsdato',
				label: 'Fødselsdato',
				subGruppe: 'Adressat',
				dataSource: DataSource.KRR,
				validation: DateValidation(false),
				inputType: InputType.Date,
				onlyShowAfterSelectedValue: { attributtId: 'adressatType', valueIndex: 3 },
				attributtType: AttributtType.SelectAndEdit
			},
			{
				hovedKategori: Kategorier.Doedsbo,
				subKategori: SubKategorier.Adressat,
				id: 'fornavn',
				label: 'Fornavn',
				subGruppe: 'Adressat',
				dataSource: DataSource.KRR,
				inputType: InputType.Text,
				validation: yup.string().required('Vennligst fyll inn navn'),
				attributtType: AttributtType.SelectAndEdit
			},
			{
				hovedKategori: Kategorier.Doedsbo,
				subKategori: SubKategorier.Adressat,
				id: 'mellomnavn',
				label: 'mellomnavn',
				subGruppe: 'Adressat',
				dataSource: DataSource.KRR,
				inputType: InputType.Text,
				attributtType: AttributtType.SelectAndEdit
			},
			{
				hovedKategori: Kategorier.Doedsbo,
				subKategori: SubKategorier.Adressat,
				id: 'etternavn',
				label: 'Etternavn',
				subGruppe: 'Adressat',
				dataSource: DataSource.KRR,
				inputType: InputType.Text,
				validation: yup.string().required('Vennligst fyll inn navn'),
				attributtType: AttributtType.SelectAndEdit
			},
			{
				hovedKategori: Kategorier.Doedsbo,
				subKategori: SubKategorier.Adressat,
				id: 'org_orgnavn',
				label: 'Organisasjonsnavn',
				subGruppe: 'Adressat',
				dataSource: DataSource.KRR,
				onlyShowAfterSelectedValue: { attributtId: 'adressatType', valueIndex: 1 },
				inputType: InputType.Text,
				attributtType: AttributtType.SelectAndEdit,
				validation: yup.string().required('Vennligst fyll inn organisasjonsnavn')
			},
			{
				hovedKategori: Kategorier.Doedsbo,
				subKategori: SubKategorier.Adressat,
				id: 'advokat_orgnavn',
				label: 'Organisasjonsnavn',
				subGruppe: 'Adressat',
				dataSource: DataSource.KRR,
				onlyShowAfterSelectedValue: { attributtId: 'adressatType', valueIndex: 0 },
				inputType: InputType.Text,
				attributtType: AttributtType.SelectAndEdit
			},
			{
				hovedKategori: Kategorier.Doedsbo,
				subKategori: SubKategorier.Adressat,
				id: 'org_orgnr',
				label: 'Organisasjonsnummer',
				subGruppe: 'Adressat',
				dataSource: DataSource.AAREG,
				inputType: InputType.Text,
				onlyShowAfterSelectedValue: { attributtId: 'adressatType', valueIndex: 1 },
				// Egen validation pga yup tror stor streng ikke er integer
				validation: yup
					.string()
					.matches(/^[0-9]*$/, 'Orgnummer må være et tall med 9 sifre')
					.test('len', 'Orgnummer må være et tall med 9 sifre', val => val && val.length === 9),
				attributtType: AttributtType.SelectAndRead
			},
			{
				hovedKategori: Kategorier.Doedsbo,
				subKategori: SubKategorier.Adressat,
				id: 'advokat_orgnr',
				label: 'Organisasjonsnummer',
				subGruppe: 'Adressat',
				dataSource: DataSource.AAREG,
				inputType: InputType.Text,
				onlyShowAfterSelectedValue: { attributtId: 'adressatType', valueIndex: 0 },
				// Egen validation pga yup tror stor streng ikke er integer
				validation: yup
					.string()
					.matches(/^[0-9]*$/, 'Orgnummer må være et tall med 9 sifre')
					.test('len', 'Orgnummer må være et tall med 9 sifre', val => val && val.length === 9),
				attributtType: AttributtType.SelectAndRead
			},
			{
				hovedKategori: Kategorier.Doedsbo,
				subKategori: SubKategorier.Adresse,
				id: 'land',
				parent: 'adresse',
				label: 'Land',
				subGruppe: 'Adresse',
				dataSource: DataSource.TPSF,
				inputType: InputType.Select,
				apiKodeverkId: 'Landkoder',
				validation: yup.string().required('Vennligst fyll inn land'),
				defaultValue: 'NOR',
				attributtType: AttributtType.SelectAndRead
			},
			{
				hovedKategori: Kategorier.Doedsbo,
				subKategori: SubKategorier.Adresse,
				id: 'adresselinje1',
				path: 'adresse.adresselinje1',
				parent: 'adresse',
				label: 'Adresselinje 1',
				subGruppe: 'Adresse',
				dataSource: DataSource.TPSF,
				inputType: InputType.Text,
				validation: yup.string().required('Vennligst fyll ut'),
				attributtType: AttributtType.SelectAndRead
			},
			{
				hovedKategori: Kategorier.Doedsbo,
				subKategori: SubKategorier.Adresse,
				id: 'adresselinje2',
				path: 'adresse.adresselinje2',
				parent: 'adresse',
				label: 'Adresselinje 2',
				subGruppe: 'Adresse',
				dataSource: DataSource.TPSF,
				inputType: InputType.Text,
				attributtType: AttributtType.SelectAndRead
			},
			{
				hovedKategori: Kategorier.Doedsbo,
				subKategori: SubKategorier.Adresse,
				id: 'postnummer',
				path: 'adresse.postnummer',
				parent: 'adresse',
				label: 'Postnummer og -sted',
				subGruppe: 'Adresse',
				dataSource: DataSource.TPSF, //endres
				apiKodeverkShowValueInLabel: true,
				inputType: InputType.Select,
				apiKodeverkId: 'Postnummer',
				validation: yup.string().required('Vennligst fyll inn postnummer/-sted'),
				attributtType: AttributtType.SelectAndEdit
			},
			{
				hovedKategori: Kategorier.Doedsbo,
				subKategori: SubKategorier.Adresse,
				id: 'utenlandsk_postnummer',
				path: 'adresse.utenlandsk_postnummer',
				parent: 'adresse',
				label: 'Postnummer',
				subGruppe: 'Adresse',
				dataSource: DataSource.TPSF, //endres
				inputType: InputType.Text,
				validation: yup
					.string()
					.matches(/^[0-9]*$/, 'Postnummeret må være et tall med 4 sifre')
					.test('len', 'Postnummeret må være et tall med 4 sifre', val => val && val.length === 4),
				attributtType: AttributtType.SelectAndEdit
			},
			{
				hovedKategori: Kategorier.Doedsbo,
				subKategori: SubKategorier.Adresse,
				id: 'utenlandsk_poststed',
				path: 'adresse.utenlandsk_poststed',
				parent: 'adresse',
				label: 'Poststed',
				subGruppe: 'Adresse',
				dataSource: DataSource.TPSF,
				validation: yup.string().required('Vennligst fyll inn poststed'),
				inputType: InputType.Text,
				attributtType: AttributtType.SelectAndRead
			},
			{
				hovedKategori: Kategorier.Doedsbo,
				subKategori: SubKategorier.Annet,
				id: 'skifteform',
				label: 'Skifteform',
				subGruppe: 'Annet',
				dataSource: DataSource.KRR,
				inputType: InputType.Select,
				validation: yup.string().required('Vennligst velg'),
				options: SelectOptionsManager('skifteform'),
				attributtType: AttributtType.SelectAndEdit
			},
			{
				hovedKategori: Kategorier.Doedsbo,
				subKategori: SubKategorier.Annet,
				id: 'utstedtDato',
				label: 'Skifteform utstedt',
				subGruppe: 'Annet',
				dataSource: DataSource.KRR,
				inputType: InputType.Date,
				validation: DateValidation(),
				attributtType: AttributtType.SelectAndEdit,
				defaultValue: new Date()
			},
			{
				hovedKategori: Kategorier.Doedsbo,
				subKategori: SubKategorier.Annet,
				id: 'gyldigFra',
				label: 'Gyldig fra',
				subGruppe: 'Annet',
				dataSource: DataSource.KRR,
				validation: DateValidation(),
				inputType: InputType.Date,
				attributtType: AttributtType.SelectAndEdit,
				defaultValue: new Date()
			},
			{
				hovedKategori: Kategorier.Doedsbo,
				subKategori: SubKategorier.Annet,
				id: 'gyldigTil',
				label: 'Gyldig til',
				subGruppe: 'Annet',
				dataSource: DataSource.KRR,
				validation: DateValidation(false),
				inputType: InputType.Date,
				attributtType: AttributtType.SelectAndEdit
			}
		]
	}
]

export default AttributtListe
