import React, { useState } from 'react'
import _get from 'lodash/get'
import { FormikProps } from 'formik'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import { Kategori } from '~/components/ui/form/kategori/Kategori'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { Postboksadresse } from './partials/midlertidigAdresse/Postboksadresse'
import { Stedsadresse } from './partials/midlertidigAdresse/Stedsadresse'
import { UtenlandskAdresse } from './partials/midlertidigAdresse/UtenlandskAdresse'
import { Gateadresse } from './partials/midlertidigAdresse/Gateadresse'

interface MidlertidigAdresse {
	formikBag: FormikProps<{}>
}

//TODO: Enum for type?
// enum AdresseTyper {
// 	CoNavn = 'CO_NAVN',
// 	LeilighetsNr = 'LEILIGHET_NR',
// 	SeksjonsNr = 'SEKSJON_NR',
// 	BoligNr = 'BOLIG_NR'
// }

export const MidlertidigAdresse = ({ formikBag }: MidlertidigAdresse) => {
	const [adressetype, setAdressetype] = useState(
		_get(formikBag.values, 'tpsf.midlertidigAdresse.adressetype')
	)

	const handleAdressetypeChange = (v: any) => {
		const type = v.value
		setAdressetype(type)
		switch (type) {
			case 'GATE':
				// TODO: Evt ha dette som initialValues?
				formikBag.setFieldValue('tpsf.midlertidigAdresse', {
					adressetype: type,
					gyldigTom: _get(formikBag.values, 'tpsf.midlertidigAdresse.gyldigTom')
				})
				break
			case 'STED':
				formikBag.setFieldValue('tpsf.midlertidigAdresse', {
					adressetype: type,
					gyldigTom: _get(formikBag.values, 'tpsf.midlertidigAdresse.gyldigTom'),
					norskAdresse: {
						postnr: '',
						eiendomsnavn: ''
					}
				})
				break
			case 'PBOX':
				formikBag.setFieldValue('tpsf.midlertidigAdresse', {
					adressetype: type,
					gyldigTom: _get(formikBag.values, 'tpsf.midlertidigAdresse.gyldigTom'),
					norskAdresse: {
						postnr: '',
						postboksnr: '',
						postboksAnlegg: ''
					}
				})
				break
			case 'UTAD':
				formikBag.setFieldValue('tpsf.midlertidigAdresse', {
					adressetype: type,
					gyldigTom: _get(formikBag.values, 'tpsf.midlertidigAdresse.gyldigTom'),
					utenlandskAdresse: {
						postLinje1: '',
						postLinje2: '',
						postLinje3: '',
						postLand: ''
						// TODO: Filtrere ut Norge
					}
				})
				break
			default:
				break
		}
	}

	return (
		<Kategori title="Midlertidig adresse" vis="tpsf.midlertidigAdresse">
			<FormikSelect
				name="tpsf.midlertidigAdresse.adressetype"
				label="Adressetype"
				options={Options('adresseType')}
				size="large"
				isClearable={false}
				onChange={handleAdressetypeChange}
			/>
			<FormikDatepicker name="tpsf.midlertidigAdresse.gyldigTom" label="Gyldig t.o.m." />
			{/* // TODO: Kan max være ett år fram i tid*/}
			{adressetype === 'GATE' && <Gateadresse formikBag={formikBag} />}
			{adressetype === 'STED' && <Stedsadresse />}
			{adressetype === 'PBOX' && <Postboksadresse />}
			{adressetype === 'UTAD' && <UtenlandskAdresse />}
		</Kategori>
	)
}
