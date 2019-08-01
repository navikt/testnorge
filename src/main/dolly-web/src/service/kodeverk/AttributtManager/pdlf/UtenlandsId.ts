import { Kategorier, SubKategorier, SubSubKategorier } from '../Categories'
import { Attributt, InputType, DataSource, AttributtType } from '../Types'
import SelectOptionsManager from '~/service/kodeverk/SelectOptionsManager/SelectOptionsManager'
import DateValidation from '~/components/fields/Datepicker/DateValidation'

import * as yup from 'yup'

const AttributtListe: Attributt[] = [
	{
		hovedKategori: Kategorier.PersInfo,
		subKategori: SubKategorier.Identifikasjon,
		id: 'utenlandskIdentifikasjonsnummer',
		label: 'Har utenlands-ID',
		subGruppe: 'true',
		path: 'identifikasjon.utenlandskIdentifikasjonsnr',
		dataSource: DataSource.PDLF,
		attributtType: AttributtType.SelectAndEdit,
		validation: yup.object(),
		items: [
			{
				hovedKategori: Kategorier.PersInfo,
				subKategori: SubKategorier.Identifikasjon,
				id: 'identifikasjonsnummer',
				label: 'Identifikasjonsnummer',
				dataSource: DataSource.PDLF,
				subGruppe: 'Utenlands-ID',
				path: 'identifikasjon.utenlandskIdentifikasjonsnr.identifikasjonsnummer',
				inputType: InputType.Text,
				validation: yup.string().required('Skriv et identifikasjonsnummer'),
				attributtType: AttributtType.SelectAndEdit
			},
			{
				hovedKategori: Kategorier.PersInfo,
				subKategori: SubKategorier.Identifikasjon,
				id: 'kilde',
				label: 'Kilde',
				dataSource: DataSource.PDLF,
				subGruppe: 'Utenlands-ID',
				path: 'identifikasjon.utenlandskIdentifikasjonsnr.kilde',
				inputType: InputType.Text,
				validation: yup.string().required('Skriv en kilde'),
				attributtType: AttributtType.SelectAndEdit
			},
			{
				hovedKategori: Kategorier.PersInfo,
				subKategori: SubKategorier.Identifikasjon,
				id: 'opphoert',
				label: 'Opphørt',
				dataSource: DataSource.PDLF,
				subGruppe: 'Utenlands-ID',
				path: 'identifikasjon.utenlandskIdentifikasjonsnr.opphoert',
				inputType: InputType.Select,
				validation: yup.string().required('Velg en verdi'),
				options: SelectOptionsManager('boolean'),
				attributtType: AttributtType.SelectAndEdit
			},
			{
				hovedKategori: Kategorier.PersInfo,
				subKategori: SubKategorier.Identifikasjon,
				id: 'utstederland',
				label: 'Utstederland',
				dataSource: DataSource.PDLF,
				subGruppe: 'Utenlands-ID',
				path: 'identifikasjon.utenlandskIdentifikasjonsnr.utstederland',
				inputType: InputType.Select,
				apiKodeverkId: 'StatsborgerskapFreg',
				validation: yup.string().required('Velg et land'),
				attributtType: AttributtType.SelectAndEdit
			}
		]
	},
	{
		hovedKategori: Kategorier.PersInfo,
		subKategori: SubKategorier.Identifikasjon,
		id: 'falskIdentitet',
		label: 'Har falsk identitet',
		subGruppe: 'true',
		path: 'identifikasjon.falskid',
		dataSource: DataSource.PDLF,
		attributtType: AttributtType.SelectAndEdit,
		validation: yup.object(),
		items: [
			{
				hovedKategori: Kategorier.PersInfo,
				subKategori: SubKategorier.Identifikasjon,
				id: 'rettIdentitet',
				label: 'Rett identitet',
				dataSource: DataSource.PDLF,
				subGruppe: 'Falsk',
				size: 'medium',
				path: 'identifikasjon.falskid.rettIdentitet',
				inputType: InputType.Select,
				options: SelectOptionsManager('rettIdentitet'),
				validation: yup.string().required('Velg rett identitet'),
				attributtType: AttributtType.SelectAndEdit
			},
			{
				hovedKategori: Kategorier.PersInfo,
				subKategori: SubKategorier.Identifikasjon,
				id: 'rettIdentifikasjonsnummer',
				label: 'Identifikasjonsnummer',
				dataSource: DataSource.PDLF,
				subGruppe: 'Falsk',
				path: 'identifikasjon.falskid.rettIdentifikasjonsnummer',
				inputType: InputType.Text,
				validation: yup
					.string()
					.required()
					.matches(/^[0-9]{11}$/, 'Id-nummer må være et tall med 11 sifre'),
				onlyShowAfterSelectedValue: { attributtId: 'rettIdentitet', valueIndex: [1] },
				attributtType: AttributtType.SelectAndEdit
			},
			{
				hovedKategori: Kategorier.PersInfo,
				subKategori: SubKategorier.Identifikasjon,
				id: 'fornavn',
				label: 'Fornavn',
				subGruppe: 'Falsk',
				path: 'identifikasjon.falskid.fornavn',
				dataSource: DataSource.PDLF,
				inputType: InputType.Text,
				validation: yup.string().required('Vennligst fyll inn navn'),
				onlyShowAfterSelectedValue: { attributtId: 'rettIdentitet', valueIndex: [2] },
				attributtType: AttributtType.SelectAndEdit
			},
			{
				hovedKategori: Kategorier.PersInfo,
				subKategori: SubKategorier.Identifikasjon,
				id: 'mellomnavn',
				label: 'mellomnavn',
				subGruppe: 'Falsk',
				path: 'identifikasjon.falskid.mellomnavn',
				dataSource: DataSource.PDLF,
				inputType: InputType.Text,
				onlyShowAfterSelectedValue: { attributtId: 'rettIdentitet', valueIndex: [2] },
				attributtType: AttributtType.SelectAndEdit
			},
			{
				hovedKategori: Kategorier.PersInfo,
				subKategori: SubKategorier.Identifikasjon,
				id: 'etternavn',
				label: 'Etternavn',
				subGruppe: 'Falsk',
				path: 'identifikasjon.falskid.etternavn',
				dataSource: DataSource.PDLF,
				inputType: InputType.Text,
				onlyShowAfterSelectedValue: { attributtId: 'rettIdentitet', valueIndex: [2] },
				validation: yup.string().required('Vennligst fyll inn navn'),
				attributtType: AttributtType.SelectAndEdit
			},
			{
				hovedKategori: Kategorier.PersInfo,
				subKategori: SubKategorier.Identifikasjon,
				id: 'foedselsdato',
				label: 'Fødselsdato',
				dataSource: DataSource.PDLF,
				subGruppe: 'Falsk',
				path: 'identifikasjon.falskid.foedselsdato',
				validation: DateValidation(false),
				inputType: InputType.Date,
				onlyShowAfterSelectedValue: { attributtId: 'rettIdentitet', valueIndex: [2] },
				attributtType: AttributtType.SelectAndEdit
			},
			{
				hovedKategori: Kategorier.PersInfo,
				subKategori: SubKategorier.Identifikasjon,
				id: 'kjonn',
				label: 'Kjønn',
				dataSource: DataSource.PDLF,
				subGruppe: 'Falsk',
				path: 'identifikasjon.falskid.kjonn',
				inputType: InputType.Select,
				apiKodeverkId: 'Kjønnstyper',
				onlyShowAfterSelectedValue: { attributtId: 'rettIdentitet', valueIndex: [2] },
				attributtType: AttributtType.SelectAndEdit
			},
			{
				hovedKategori: Kategorier.PersInfo,
				subKategori: SubKategorier.Identifikasjon,
				id: 'statsborgerskap',
				label: 'Statsborgerskap',
				subGruppe: 'Falsk',
				path: 'identifikasjon.falskid.statsborgerskap',
				isMultiple: true,
				dataSource: DataSource.PDLF,
				inputType: InputType.Select,
				apiKodeverkId: 'StatsborgerskapFreg',
				onlyShowAfterSelectedValue: { attributtId: 'rettIdentitet', valueIndex: [2] },
				validation: yup.string().required('Vennligst velg minst ett statsborgerskap'),
				attributtType: AttributtType.SelectAndEdit
			}
		]
	}
]

export default AttributtListe
