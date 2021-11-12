import React, { useState } from 'react'
import _get from 'lodash/get'
import { FormikProps } from 'formik'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import { Kategori } from '~/components/ui/form/kategori/Kategori'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { Postboksadresse } from './partials/midlertidigAdresse/Postboksadresse'
import { Stedsadresse } from './partials/midlertidigAdresse/Stedsadresse'
import { UtenlandskAdresse } from './partials/midlertidigAdresse/UtenlandskAdresse'
import { Gateadresse } from './partials/midlertidigAdresse/Gateadresse'
import { Tilleggsadresse } from '~/components/fagsystem/tpsf/form/adresser/partials/Tilleggsadresse/Tilleggsadresse'
import { DollySelect } from '~/components/ui/form/inputs/select/Select'

interface MidlertidigAdresseProps {
	formikBag: FormikProps<{}>
}

interface Adressetype {
	label: string
	lowercaseLabel: string
	value: string
}

enum AdresseTyper {
	GATE = 'GATE',
	STED = 'STED',
	PBOX = 'PBOX',
	UTAD = 'UTAD',
}

export const MidlertidigAdresse = ({ formikBag }: MidlertidigAdresseProps) => {
	const adressetypePath = 'tpsf.midlertidigAdresse.adressetype'
	const gyldigTom = 'tpsf.midlertidigAdresse.gyldigTom'
	const tilleggsadresse = 'tpsf.midlertidigAdresse.norskAdresse.tilleggsadresse'

	const [adressetype, setAdressetype] = useState(_get(formikBag.values, adressetypePath))

	const handleAdressetypeChange = (valgt: Adressetype) => {
		const type = valgt.value
		setAdressetype(type)
		switch (type) {
			case AdresseTyper.GATE:
				formikBag.setFieldValue('tpsf.midlertidigAdresse', {
					adressetype: type,
					gyldigTom: _get(formikBag.values, gyldigTom),
					norskAdresse: {
						tilleggsadresse: _get(formikBag.values, tilleggsadresse),
					},
				})
				break
			case AdresseTyper.STED:
				formikBag.setFieldValue('tpsf.midlertidigAdresse', {
					adressetype: type,
					gyldigTom: _get(formikBag.values, gyldigTom),
					norskAdresse: {
						postnr: '',
						eiendomsnavn: '',
						tilleggsadresse: _get(formikBag.values, tilleggsadresse),
					},
				})
				break
			case AdresseTyper.PBOX:
				formikBag.setFieldValue('tpsf.midlertidigAdresse', {
					adressetype: type,
					gyldigTom: _get(formikBag.values, gyldigTom),
					norskAdresse: {
						postnr: '',
						postboksnr: '',
						postboksAnlegg: '',
					},
				})
				break
			case AdresseTyper.UTAD:
				formikBag.setFieldValue('tpsf.midlertidigAdresse', {
					adressetype: type,
					gyldigTom: _get(formikBag.values, gyldigTom),
					utenlandskAdresse: {
						postLinje1: '',
						postLinje2: '',
						postLinje3: '',
						postLand: '',
					},
				})
				break
			default:
				break
		}
	}

	return (
		<Kategori title="Midlertidig adresse" vis="tpsf.midlertidigAdresse">
			<DollySelect
				name={adressetypePath}
				label="Adressetype"
				value={_get(formikBag.values, adressetypePath)}
				options={Options('adresseType')}
				size="large"
				isClearable={false}
				onChange={handleAdressetypeChange}
			/>
			<FormikDatepicker name={gyldigTom} label="Gyldig t.o.m." />
			{adressetype === AdresseTyper.GATE && <Gateadresse formikBag={formikBag} />}
			{adressetype === AdresseTyper.STED && <Stedsadresse />}
			{adressetype === AdresseTyper.PBOX && <Postboksadresse />}
			{adressetype === AdresseTyper.UTAD && <UtenlandskAdresse />}
			{(adressetype === AdresseTyper.GATE || adressetype === AdresseTyper.STED) && (
				<Tilleggsadresse
					formikBag={formikBag}
					tilleggsadressePath={tilleggsadresse}
					options="tilleggstypeMidlertidig"
					type="midlertidig "
				/>
			)}
		</Kategori>
	)
}
