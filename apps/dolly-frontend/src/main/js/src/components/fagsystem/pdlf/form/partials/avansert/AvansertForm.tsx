import React from 'react'
import useBoolean from '~/utils/hooks/useBoolean'
import Button from '~/components/ui/button/Button'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import { FormikCheckbox } from '~/components/ui/form/inputs/checbox/Checkbox'

export const AvansertForm = ({ path }) => {
	const [visAvansert, setVisAvansert, setSkjulAvansert] = useBoolean(false)

	return (
		<div className="flexbox--full-width">
			{visAvansert ? (
				<Button onClick={setSkjulAvansert} kind={'collapse'}>
					SKJUL AVANSERT
				</Button>
			) : (
				<Button onClick={setVisAvansert} kind={'expand'}>
					VIS AVANSERT
				</Button>
			)}
			{visAvansert && (
				<div className={'flexbox--flex-wrap'} style={{ marginTop: '10px' }}>
					<FormikTextInput name={`${path}.kilde`} label="Kilde" />
					<FormikSelect name={`${path}.master`} label="Master" options={Options('master')} />
					<FormikCheckbox name={`${path}.gjeldende`} label="Er gjeldende" checkboxMargin />
				</div>
			)}
		</div>
	)
}
