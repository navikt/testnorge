import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { FormDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { initialTimeloennet } from '../initialValues'

const infotekst =
	'Start- og sluttdato må både være innenfor samme kalendermåned i samme år og perioden til arbeidsforholdet'

export const TimeloennetForm = ({ path }) => {
	return (
		<FormDollyFieldArray
			name={path}
			header="Timer med timelønnet"
			hjelpetekst={infotekst}
			newEntry={initialTimeloennet}
			nested
		>
			{(partialPath, idx) => (
				<div key={idx} className="flexbox">
					<FormTextInput
						name={`${partialPath}.antallTimer`}
						label="Antall timer for timelønnet"
						type="number"
					/>
					<FormDatepicker name={`${partialPath}.periode.fom`} label="Periode fra" />
					<FormDatepicker name={`${partialPath}.periode.tom`} label="Periode til" />
				</div>
			)}
		</FormDollyFieldArray>
	)
}
