import React, { useState } from 'react'
import { FormikProps } from 'formik'
import { DollyCheckbox } from '~/components/ui/form/inputs/checbox/Checkbox'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import './tilleggsadresse.less'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import _get from 'lodash/get'

interface TilleggsadresseProps {
	formikBag: FormikProps<{ tpsf: BoadresseValues }>
}

interface BoadresseValues {
	boadresse: TilleggsadresseValues
}

interface TilleggsadresseValues {
	tilleggsadresse?: {
		tilleggType: string
		nummer?: number
	}
}

enum TilleggsTyper {
	CoNavn = 'CO_NAVN',
	LeilighetsNr = 'LEILIGHET_NR',
	SeksjonsNr = 'SEKSJON_NR',
	BoligNr = 'BOLIG_NR'
}

const initialValues = { tilleggType: '', nummer: '' }

export const Tilleggsadresse = ({ formikBag }: TilleggsadresseProps) => {
	const tilleggsadressePath = 'tpsf.boadresse.tilleggsadresse'

	const [showTilleggsadresse, setShowTilleggsadresse] = useState(
		formikBag.values.tpsf.boadresse.hasOwnProperty('tilleggsadresse') ? true : false
	)

	const handleChangeTilleggsadresse = () => {
		if (!showTilleggsadresse) {
			formikBag.setFieldValue(tilleggsadressePath, initialValues)
		} else {
			formikBag.setFieldValue(tilleggsadressePath, undefined)
		}
		setShowTilleggsadresse(!showTilleggsadresse)
	}

	const handleChangeTilleggstype = (type: { value: string; label: string }) => {
		formikBag.setFieldValue(`${tilleggsadressePath}.tilleggType`, type.value)
		if (type.value === TilleggsTyper.CoNavn) {
			formikBag.setFieldValue(`${tilleggsadressePath}.nummer`, '')
		}
	}

	const showNummer = () => {
		const tilleggstype = _get(formikBag.values, `${tilleggsadressePath}.tilleggType`)
		return tilleggstype !== TilleggsTyper.CoNavn && tilleggstype !== ''
	}

	return (
		<>
			<div className="tilleggsadresse-form__switch">
				<h3>Legg til tilleggsadresse </h3>
				{
					// @ts-ignore
					<DollyCheckbox
						name="aktiver-tilleggsadresse"
						onChange={handleChangeTilleggsadresse}
						label="Legg til tilleggsadresse"
						checked={showTilleggsadresse}
						isSwitch
					/>
				}
			</div>
			{showTilleggsadresse && (
				<div className="flexbox--flex-wrap">
					<FormikSelect
						name={`${tilleggsadressePath}.tilleggType`}
						label="Tilleggstype"
						options={Options('tilleggstype')}
						onChange={handleChangeTilleggstype}
						isClearable={false}
					/>
					{showNummer() && (
						<FormikTextInput name={`${tilleggsadressePath}.nummer`} label="Nummer" type="number" />
					)}
				</div>
			)}
		</>
	)
}
