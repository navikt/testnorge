import useBoolean from '@/utils/hooks/useBoolean'
import Button from '@/components/ui/button/Button'
import { FormikTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { FormikSelect } from '@/components/ui/form/inputs/select/Select'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'

interface AvansertFormValues {
	path: string
	kanVelgeMaster?: boolean
	handleChange?: Function
}

export const AvansertForm = ({
	path,
	kanVelgeMaster = true,
	handleChange = null,
}: AvansertFormValues) => {
	const [visAvansert, setVisAvansert, setSkjulAvansert] = useBoolean(false)

	return (
		<div className="flexbox--full-width">
			{visAvansert ? (
				<Button onClick={setSkjulAvansert} kind={'collapse'}>
					SKJUL AVANSERTE VALG
				</Button>
			) : (
				<Button onClick={setVisAvansert} kind={'expand'}>
					VIS AVANSERTE VALG
				</Button>
			)}
			{visAvansert && (
				<div className={'flexbox--flex-wrap'} style={{ marginTop: '10px' }}>
					<FormikTextInput name={`${path}.kilde`} label="Kilde" />
					<FormikSelect
						name={`${path}.master`}
						label="Master"
						options={Options('master')}
						isDisabled={!kanVelgeMaster}
						// onChange={(target) => handleChange(target, path) || undefined}
					/>
					{/*Gjeldende skjules frem til vi finner en måte å håndtere den på*/}
					{/*<FormikCheckbox name={`${path}.gjeldende`} label="Er gjeldende" checkboxMargin />*/}
				</div>
			)}
		</div>
	)
}
