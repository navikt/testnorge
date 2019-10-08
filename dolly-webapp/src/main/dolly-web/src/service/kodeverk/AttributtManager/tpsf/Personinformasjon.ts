import { Kategorier, SubKategorier } from '../Categories'
import { Attributt, InputType, DataSource, AttributtType } from '../Types'
import SelectOptionsManager from '~/service/kodeverk/SelectOptionsManager/SelectOptionsManager'
import DateValidation from '~/components/fields/Datepicker/DateValidation'
import * as yup from 'yup'

const AttributtListe: Attributt[] = [
	{
		hovedKategori: Kategorier.PersInfo,
		subKategori: SubKategorier.Alder,
		id: 'foedtEtter',
		label: 'Født etter',
		dataSource: DataSource.TPSF,
		inputType: InputType.Date,
		validation: DateValidation(false),
		attributtType: AttributtType.SelectOnly,
		sattForEksisterendeIdent: true,
		defaultValue: new Date().setFullYear(new Date().getFullYear() - 80)
	},
	{
		hovedKategori: Kategorier.PersInfo,
		subKategori: SubKategorier.Alder,
		id: 'foedtFoer',
		label: 'Født før',
		dataSource: DataSource.TPSF,
		inputType: InputType.Date,
		validation: DateValidation(false),
		attributtType: AttributtType.SelectOnly,
		sattForEksisterendeIdent: true,
		defaultValue: new Date()
	},
	{
		hovedKategori: Kategorier.PersInfo,
		subKategori: SubKategorier.Alder,
		id: 'doedsdato',
		label: 'Dødsdato',
		dataSource: DataSource.TPSF,
		inputType: InputType.Date,
		validation: DateValidation(false),
		attributtType: AttributtType.SelectOnly
		//defaultValue: new Date()
	},
	{
		hovedKategori: Kategorier.PersInfo,
		subKategori: SubKategorier.Nasjonalitet,
		id: 'statsborgerskap',
		label: 'Statsborgerskap',
		dataSource: DataSource.TPSF,
		inputType: InputType.Select,
		apiKodeverkId: 'Landkoder',
		attributtType: AttributtType.SelectAndEdit
	},
	{
		hovedKategori: Kategorier.PersInfo,
		subKategori: SubKategorier.Nasjonalitet,
		id: 'innvandret',
		label: 'Innvandret fra',
		dataSource: DataSource.TPSF,
		attributtType: AttributtType.SelectOnly,
		validation: yup.object(),
		items: [
			{
				hovedKategori: Kategorier.PersInfo,
				subKategori: SubKategorier.Nasjonalitet,
				id: 'innvandretFraLand',
				label: 'Innvandret fra land',
				dataSource: DataSource.TPSF,
				inputType: InputType.Select,
				editPath: 'innvandretFraLand',
				//validation: yup.string().required('Vennligst velg'),
				apiKodeverkId: 'Landkoder',
				attributtType: AttributtType.SelectAndEdit
			},
			{
				hovedKategori: Kategorier.PersInfo,
				subKategori: SubKategorier.Nasjonalitet,
				id: 'innvandretFraLandFlyttedato',
				label: 'Innvandret dato',
				dataSource: DataSource.TPSF,
				inputType: InputType.Date,
				editPath: 'innvandretTilLandFlyttedato',
				attributtType: AttributtType.SelectAndEdit
			}
		]
	},
	{
		hovedKategori: Kategorier.PersInfo,
		subKategori: SubKategorier.Nasjonalitet,
		id: 'utvandret',
		label: 'Utvandret til',
		dataSource: DataSource.TPSF,
		attributtType: AttributtType.SelectAndEdit,
		validation: yup.object(),
		items: [
			{
				hovedKategori: Kategorier.PersInfo,
				subKategori: SubKategorier.Nasjonalitet,
				id: 'utvandretTilLand',
				label: 'Utvandret til land',
				dataSource: DataSource.TPSF,
				inputType: InputType.Select,
				editPath: 'utvandretTilLand',
				validation: yup.string().required('Vennligst velg'),
				apiKodeverkId: 'Landkoder',
				attributtType: AttributtType.SelectAndEdit
			},
			{
				hovedKategori: Kategorier.PersInfo,
				subKategori: SubKategorier.Nasjonalitet,
				id: 'utvandretTilLandFlyttedato',
				label: 'Utvandret dato',
				dataSource: DataSource.TPSF,
				inputType: InputType.Date,
				editPath: 'utvandretTilLandFlyttedato',
				attributtType: AttributtType.SelectAndEdit
			}
		]
	},
	{
		hovedKategori: Kategorier.PersInfo,
		subKategori: SubKategorier.Diverse,
		id: 'kjonn',
		label: 'Kjønn',
		dataSource: DataSource.TPSF,
		inputType: InputType.Select,
		apiKodeverkId: 'Kjønnstyper',
		attributtType: AttributtType.SelectOnly,
		sattForEksisterendeIdent: true
	},
	{
		hovedKategori: Kategorier.PersInfo,
		subKategori: SubKategorier.Diverse,
		id: 'harMellomnavn',
		label: 'Mellomnavn',
		dataSource: DataSource.TPSF,
		inputType: InputType.Select,
		options: SelectOptionsManager('boolean'),
		attributtType: AttributtType.SelectAndEdit
	},
	{
		hovedKategori: Kategorier.PersInfo,
		subKategori: SubKategorier.Diverse,
		id: 'sivilstand',
		label: 'Sivilstand',
		dataSource: DataSource.TPSF,
		inputType: InputType.Select,
		apiKodeverkId: 'Sivilstander',
		attributtType: AttributtType.SelectAndEdit
	},
	{
		hovedKategori: Kategorier.PersInfo,
		subKategori: SubKategorier.Diverse,
		id: 'sprakKode',
		label: 'Språk',
		dataSource: DataSource.TPSF,
		inputType: InputType.Select,
		apiKodeverkId: 'Språk',
		attributtType: AttributtType.SelectAndEdit
	},
	{
		hovedKategori: Kategorier.PersInfo,
		subKategori: SubKategorier.Diverse,
		id: 'egenAnsattDatoFom',
		label: 'Egenansatt',
		dataSource: DataSource.TPSF,
		inputType: InputType.Select,
		options: SelectOptionsManager('boolean'),
		attributtType: AttributtType.SelectAndEdit,
		transform: egenAnsatt => (egenAnsatt ? new Date() : null)
	},
	{
		hovedKategori: Kategorier.PersInfo,
		subKategori: SubKategorier.Diverse,
		id: 'utenFastBopel',
		label: 'Uten fast bopel',
		dataSource: DataSource.TPSF,
		inputType: InputType.Select,
		options: SelectOptionsManager('boolean'),
		attributtType: AttributtType.SelectOnly
	},
	{
		hovedKategori: Kategorier.PersInfo,
		subKategori: SubKategorier.Diverse,
		id: 'ufb_kommunenr',
		label: 'Kommunenummer',
		dataSource: DataSource.TPSF,
		inputType: InputType.Select,
		attributtType: AttributtType.EditOnly
	},
	{
		hovedKategori: Kategorier.PersInfo,
		subKategori: SubKategorier.Diverse,
		id: 'spesreg',
		label: 'Diskresjonskode',
		dataSource: DataSource.TPSF,
		inputType: InputType.Select,
		hoydeOptions: 'large',
		apiKodeverkId: 'Diskresjonskoder',
		attributtType: AttributtType.SelectAndEdit
	},
	{
		hovedKategori: Kategorier.PersInfo,
		subKategori: SubKategorier.Identifikasjon,
		id: 'identHistorikk',
		label: 'Har identhistorikk',
		subGruppe: 'true',
		dataSource: DataSource.TPSF,
		attributtType: AttributtType.SelectOnly,
		isMultiple: true,
		informasjonstekst:
			'Dette er en oversikt over identhistorikken, altså utdaterte identer. ' +
			'Dagens identtype legges inn på forrige side. For å velge dagens kjønn må "Kjønn" hukes av på forrige side og velges under "Diverse" -> "Kjønn" her. ' +
			'Eksempel: En testperson med DNR får FNR. Da velges FNR på forrige side. DNR legges inn i denne oversikten. ' +
			'Hvis fødselsdatoen ble endret i overgangen kan født før og født etter fylles ut. Det samme gjelder for kjønn. ' +
			'Dersom de står som "Ikke spesifisert" beholdes samme fødselsdato og/eller kjønn.',
		validation: yup.object(),
		items: [
			{
				hovedKategori: Kategorier.PersInfo,
				subKategori: SubKategorier.Identifikasjon,
				id: 'identtype',
				label: 'Identtype',
				subGruppe: 'Identhistorikk',
				path: 'identHistorikk.identtype',
				dataSource: DataSource.TPSF,
				inputType: InputType.Select,
				options: SelectOptionsManager('identtype'),
				attributtType: AttributtType.SelectOnly
			},
			{
				hovedKategori: Kategorier.PersInfo,
				subKategori: SubKategorier.Identifikasjon,
				id: 'kjonn',
				label: 'Kjønn',
				subGruppe: 'Identhistorikk',
				path: 'identHistorikk.kjonn',
				dataSource: DataSource.TPSF,
				inputType: InputType.Select,
				apiKodeverkId: 'Kjønnstyper',
				attributtType: AttributtType.SelectOnly
			},
			{
				hovedKategori: Kategorier.PersInfo,
				subKategori: SubKategorier.Identifikasjon,
				id: 'regdato',
				label: 'Utgått dato',
				subGruppe: 'Identhistorikk',
				path: 'identHistorikk.regdato',
				dataSource: DataSource.TPSF,
				inputType: InputType.Date,
				validation: DateValidation(false),
				attributtType: AttributtType.SelectOnly
			},
			{
				hovedKategori: Kategorier.PersInfo,
				subKategori: SubKategorier.Identifikasjon,
				id: 'foedtFoer',
				label: 'Født før',
				subGruppe: 'Identhistorikk',
				path: 'identHistorikk.foedtFoer',
				dataSource: DataSource.TPSF,
				inputType: InputType.Date,
				validation: DateValidation(false),
				attributtType: AttributtType.SelectOnly
			},
			{
				hovedKategori: Kategorier.PersInfo,
				subKategori: SubKategorier.Identifikasjon,
				id: 'foedtEtter',
				label: 'Født etter',
				subGruppe: 'Identhistorikk',
				path: 'identHistorikk.foedtEtter',
				dataSource: DataSource.TPSF,
				inputType: InputType.Date,
				validation: DateValidation(false),
				attributtType: AttributtType.SelectOnly
			}
		]
	},
	{
		hovedKategori: Kategorier.PersInfo,
		subKategori: SubKategorier.Diverse,
		id: 'forsvunnet',
		label: 'Forsvunnet',
		dataSource: DataSource.TPSF,
		inputType: InputType.Select,
		attributtType: AttributtType.SelectOnly,
		items: [
			{
				hovedKategori: Kategorier.PersInfo,
				subKategori: SubKategorier.Diverse,
				id: 'erForsvunnet',
				label: 'Forsvunnet',
				dataSource: DataSource.TPSF,
				inputType: InputType.Select,
				options: SelectOptionsManager('stringBoolean'),
				attributtType: AttributtType.SelectOnly,
				editPath: 'erForsvunnet'
			},
			{
				hovedKategori: Kategorier.PersInfo,
				subKategori: SubKategorier.Diverse,
				id: 'forsvunnetDato',
				label: 'ForsvunnetDato',
				dataSource: DataSource.TPSF,
				inputType: InputType.Date,
				validation: DateValidation(),
				attributtType: AttributtType.SelectOnly,
				editPath: 'forsvunnetDato'
			}
		]
	}
]

export default AttributtListe
