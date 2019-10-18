import { Kategorier, SubKategorier } from '../Categories'
import { Attributt, InputType, DataSource, AttributtType } from '../Types'
import SelectOptionsManager from '~/service/kodeverk/SelectOptionsManager/SelectOptionsManager'
import DateValidation from '~/components/fields/Datepicker/DateValidation'

import * as yup from 'yup'

const AttributtListe: Attributt[] = [
	{
		hovedKategori: Kategorier.KontaktInfo,
		subKategori: SubKategorier.Krr,
		id: 'krr',
		label: 'Har kontaktinformasjon',
		dataSource: DataSource.KRR,
		attributtType: AttributtType.SelectAndEdit,
		validation: yup.object(),
		items: [
			{
				hovedKategori: Kategorier.KontaktInfo,
				subKategori: SubKategorier.Krr,
				id: 'mobil',
				label: 'Mobilnummer',
				dataSource: DataSource.KRR,
				inputType: InputType.Text,
				validation: yup.string().matches(/^[0-9]*$/, 'Ugyldig mobilnummer'),
				attributtType: AttributtType.SelectAndEdit
			},
			{
				hovedKategori: Kategorier.KontaktInfo,
				subKategori: SubKategorier.Krr,
				id: 'epost',
				label: 'E-postadresse',
				dataSource: DataSource.KRR,
				inputType: InputType.Text,
				validation: yup.string(),
				attributtType: AttributtType.SelectAndEdit
			},
			{
				hovedKategori: Kategorier.KontaktInfo,
				subKategori: SubKategorier.Krr,
				id: 'registrert',
				dataSource: DataSource.KRR,
				label: 'Registrert i DKIF',
				inputType: InputType.Select,
				options: SelectOptionsManager('stringBoolean'),
				attributtType: AttributtType.SelectAndEdit
			},
			{
				hovedKategori: Kategorier.KontaktInfo,
				subKategori: SubKategorier.Krr,
				id: 'reservert',
				dataSource: DataSource.KRR,
				label: 'Reservert mot digitalkommunikasjon',
				inputType: InputType.Select,
				validation: yup.string().required('Vennligst velg'),
				options: SelectOptionsManager('stringBoolean'),
				attributtType: AttributtType.SelectAndEdit
			},
			{
				hovedKategori: Kategorier.KontaktInfo,
				subKategori: SubKategorier.Krr,
				id: 'gyldigFra',
				label: 'Gyldig fra',
				dataSource: DataSource.KRR,
				validation: DateValidation(false),
				inputType: InputType.Date,
				attributtType: AttributtType.SelectAndEdit
			}
		]
	}
]

export default AttributtListe
