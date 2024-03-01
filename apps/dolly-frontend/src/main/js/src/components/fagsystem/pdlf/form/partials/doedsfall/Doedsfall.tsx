import { FormikDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { AvansertForm } from '@/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'
import { FormikDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { initialDoedsfall } from '@/components/fagsystem/pdlf/form/initialValues'

type DoedsfallTypes = {
	path: string
}

export const DoedsfallForm = ({ path }: DoedsfallTypes) => {
	return (
		<>
			<FormikDatepicker name={`${path}.doedsdato`} label="Dødsdato" maxDate={new Date()} />
			<AvansertForm path={path} kanVelgeMaster={false} />
		</>
	)
}

export const Doedsfall = () => {
	return (
		<div className="flexbox--flex-wrap">
			<FormikDollyFieldArray
				name={'pdldata.person.doedsfall'}
				header="Dødsfall"
				newEntry={initialDoedsfall}
				canBeEmpty={false}
			>
				{(path: string, _idx: number) => <DoedsfallForm path={path} />}
			</FormikDollyFieldArray>
		</div>
	)
}
