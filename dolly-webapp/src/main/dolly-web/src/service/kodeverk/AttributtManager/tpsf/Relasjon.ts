import { Kategorier, SubKategorier } from '../Categories'
import { Attributt, InputType, DataSource } from '../Types'

import * as yup from 'yup'

const AttributtListe: Attributt[] = [
	{
		hovedKategori: Kategorier.FamilieRelasjoner,
		subKategori: SubKategorier.Boadresse,
		id: 'relasjon.barn',
		label: 'Har barn',
		dataSource: DataSource.TPSF,
		inputType: InputType.Text,
		validation: yup.string()
	}
]

export default AttributtListe
