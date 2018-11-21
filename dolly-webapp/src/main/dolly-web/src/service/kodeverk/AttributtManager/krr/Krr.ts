import { Kategorier, SubKategorier } from '../Categories'
import { Attributt, InputType, DataSource } from '../Types'
import SelectOptionsManager from '~/service/kodeverk/SelectOptionsManager/SelectOptionsManager'

import * as yup from 'yup'

const AttributtListe: Attributt[] = [
	{
		hovedKategori: Kategorier.KontaktInfo,
		subKategori: SubKategorier.Krr,
		id: 'mobil',
		label: 'Mobilnummer',
		dataSource: DataSource.KRR,
		inputType: InputType.Text,
		validation: yup.string().required('Vennligst oppgi mobilnummer'),
		kanRedigeres: true
	},
	{
		hovedKategori: Kategorier.KontaktInfo,
		subKategori: SubKategorier.Krr,
		id: 'epost',
		label: 'E-postadresse',
		dataSource: DataSource.KRR,
		inputType: InputType.Text,
		validation: yup.string().email('Vennligst fyll ut'),
		kanRedigeres: true
	},
	{
		hovedKategori: Kategorier.KontaktInfo,
		subKategori: SubKategorier.Krr,
		id: 'reservert',
		dataSource: DataSource.KRR,
		label: 'Reservert mot digitalkommmunikasjon',
		inputType: InputType.Select,
		options: SelectOptionsManager('boolean'),
		validation: yup.string().required('Vennligst velg en verdi'),
		kanRedigeres: true
	}
]

export default AttributtListe
