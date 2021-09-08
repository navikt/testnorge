import React, { useState } from 'react'
import _get from 'lodash/get'
import { FormikProps } from 'formik'
import { DollyCheckbox } from '~/components/ui/form/inputs/checbox/Checkbox'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import './tilleggsadresse.less'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'

interface TilleggsadresseProps {
	formikBag: FormikProps<{ tpsf?: AdresseValues }>
	tilleggsadressePath: string
	options: string
	type?: string
}

interface AdresseValues {
	boadresse?: TilleggsadresseValues
	midlertidigAdresse?: TilleggsadresseValues
}

interface TilleggsadresseValues {
	tilleggsadresse: {
		tilleggType: string
		nummer?: number
	}
}

enum TilleggsTyper {
	CoNavn = 'CO_NAVN',
	LeilighetsNr = 'LEILIGHET_NR',
	SeksjonsNr = 'SEKSJON_NR',
	BoligNr = 'BOLIG_NR',
}

const initialValues = { tilleggType: '', nummer: '' }

export const Tilleggsadresse = ({
	formikBag,
	tilleggsadressePath,
	options,
	type,
}: TilleggsadresseProps) => {
	const [showTilleggsadresse, setShowTilleggsadresse] = useState(
		_get(formikBag.values, tilleggsadressePath) ? true : false
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
				<h3>Legg til {type}tilleggsadresse </h3>
				{
					// @ts-ignore
					<DollyCheckbox
						name="aktiver-tilleggsadresse"
						onChange={handleChangeTilleggsadresse}
						label={`Legg til ${type}tilleggsadresse`}
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
						options={Options(options)}
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
