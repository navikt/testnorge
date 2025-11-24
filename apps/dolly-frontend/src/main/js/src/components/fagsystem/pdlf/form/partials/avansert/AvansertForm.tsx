import useBoolean from '@/utils/hooks/useBoolean'
import Button from '@/components/ui/button/Button'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { useFormContext } from 'react-hook-form'

interface AvansertFormValues {
	path: string
	kanVelgeMaster?: boolean
}

export const AvansertForm = ({ path, kanVelgeMaster = true }: AvansertFormValues) => {
	const formMethods = useFormContext()
	const [visAvansert, setVisAvansert, setSkjulAvansert] = useBoolean(false)
	const parentPath = path.substring(0, path.lastIndexOf('.'))

	return (
		<div className="flexbox--full-width">
			{visAvansert ? (
				<Button onClick={setSkjulAvansert} kind={'chevron-up'}>
					SKJUL AVANSERTE VALG
				</Button>
			) : (
				<Button onClick={setVisAvansert} kind={'chevron-down'}>
					VIS AVANSERTE VALG
				</Button>
			)}
			{visAvansert && (
				<div className={'flexbox--flex-wrap'} style={{ marginTop: '10px' }}>
					<FormTextInput name={`${path}.kilde`} label="Kilde" />
					<FormSelect
						name={`${path}.master`}
						label="Master"
						options={Options('master')}
						isDisabled={!kanVelgeMaster}
						afterChange={() => formMethods.trigger(parentPath)}
					/>
					{/*Gjeldende skjules frem til vi finner en måte å håndtere den på*/}
					{/*<FormCheckbox name={`${path}.gjeldende`} label="Er gjeldende" checkboxMargin />*/}
				</div>
			)}
		</div>
	)
}
