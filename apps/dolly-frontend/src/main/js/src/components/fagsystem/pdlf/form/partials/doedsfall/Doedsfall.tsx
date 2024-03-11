import { FormDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { AvansertForm } from '@/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'
import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { initialDoedsfall } from '@/components/fagsystem/pdlf/form/initialValues'

type DoedsfallTypes = {
	path: string
}

export const DoedsfallForm = ({ path }: DoedsfallTypes) => {
	return (
		<>
			<FormDatepicker name={`${path}.doedsdato`} label="Dødsdato" maxDate={new Date()} />
			<AvansertForm path={path} kanVelgeMaster={false} />
		</>
	)
}

export const Doedsfall = () => {
	return (
		<div className="flexbox--flex-wrap">
			<FormDollyFieldArray
				name={'pdldata.person.doedsfall'}
				header="Dødsfall"
				newEntry={initialDoedsfall}
				canBeEmpty={false}
			>
				{(path: string, _idx: number) => <DoedsfallForm path={path} />}
			</FormDollyFieldArray>
		</div>
	)
}
