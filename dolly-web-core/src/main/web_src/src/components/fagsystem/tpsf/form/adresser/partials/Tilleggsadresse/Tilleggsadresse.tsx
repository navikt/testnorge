import React, { useState } from 'react'
import { FormikProps } from 'formik'
import { DollyCheckbox } from '~/components/ui/form/inputs/checbox/Checkbox'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import './tilleggsadresse.less'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import _get from 'lodash/get'

interface TilleggsadresseProps {
	formikBag: FormikProps<{}>
}

enum TilleggsTyper {
	CoNavn = 'CO_NAVN',
	LeilighetsNr = 'LEILIGHET_NR',
	SeksjonsNr = 'SEKSJON_NR',
	BoligNr = 'BOLIG_NR'
}

const initialValues = { tilleggType: '', nummer: '' }

export const Tilleggsadresse = ({ formikBag }: TilleggsadresseProps) => {
	const [showTilleggsadresse, setShowTilleggsadresse] = useState(false)
	const [tilfeldigErValgt, setTilfeldigErValgt] = useState(false)

	const handleChangeTilleggsadresse = () => {
		if (!showTilleggsadresse) {
			formikBag.setFieldValue('tpsf.tilleggsadresse', initialValues)
		} else {
			formikBag.setFieldValue('tpsf.tilleggsadresse', {})
		}
		setShowTilleggsadresse(!showTilleggsadresse)
	}

	const handleChangeTilleggstype = (type: { value: string; label: string }) => {
		formikBag.setFieldValue('tpsf.tilleggsadresse.tilleggType', type.value)
		if (type.value === TilleggsTyper.CoNavn) {
			formikBag.setFieldValue('tpsf.tilleggsadresse.nummer', '')
		}
	}

	const handleVelgTilfeldigAdresse = () => {
		setTilfeldigErValgt(!tilfeldigErValgt)
		formikBag.setFieldValue('tpsf.tilleggsadresse', initialValues)
	}

	const showNummer = () => {
		const tilleggstype = _get(formikBag.values, 'tpsf.tilleggsadresse.tilleggType')
		console.log(tilleggstype)
		return tilleggstype !== TilleggsTyper.CoNavn && tilleggstype !== ''
	}

	return (
		<div className="tilleggsadresse-form">
			<div className="tilleggsadresse-form__switch">
				<h3>Legg til tilleggsadresse </h3>
				{
					// @ts-ignore
					<DollyCheckbox
						name="aktiver-tilleggsadresse"
						onChange={handleChangeTilleggsadresse}
						label="Legg til tilleggsadresse"
						isSwitch
					/>
				}
			</div>
			{showTilleggsadresse && (
				// @ts-ignore
				<DollyCheckbox
					name="tilfeldig-tilleggsadresse"
					onChange={handleVelgTilfeldigAdresse}
					label={'Tilfeldig adresse'}
				/>
			)}
			{showTilleggsadresse && !tilfeldigErValgt && (
				<div className="tilleggsadresse-form__options">
					<FormikSelect
						name="tpsf.tilleggsadresse.tilleggType"
						label="Tilleggstype"
						options={Options('tilleggstype')}
						onChange={handleChangeTilleggstype}
					/>
					{showNummer() && (
						<FormikTextInput name="tpsf.tilleggsadresse.nummer" label="Nummer" type="number" />
					)}
				</div>
			)}
		</div>
	)
}
