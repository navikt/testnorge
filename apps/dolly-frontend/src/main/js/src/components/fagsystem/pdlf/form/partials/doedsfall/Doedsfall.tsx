import { FormDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { AvansertForm } from '@/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'
import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { getInitialDoedsfall } from '@/components/fagsystem/pdlf/form/initialValues'
import { useContext } from 'react'
import {
	BestillingsveilederContext,
	BestillingsveilederContextType,
} from '@/components/bestillingsveileder/BestillingsveilederContext'

type DoedsfallTypes = {
	path: string
}

export const DoedsfallForm = ({ path }: DoedsfallTypes) => {
	const opts = useContext(BestillingsveilederContext) as BestillingsveilederContextType
	return (
		<>
			<FormDatepicker name={`${path}.doedsdato`} label="Dødsdato" maxDate={new Date()} />
			<AvansertForm
				path={path}
				kanVelgeMaster={opts?.identMaster !== 'PDL' && opts?.identtype !== 'NPID'}
			/>
		</>
	)
}

export const Doedsfall = () => {
	return (
		<div className="flexbox--flex-wrap">
			<FormDollyFieldArray
				name={'pdldata.person.doedsfall'}
				header="Dødsfall"
				newEntry={getInitialDoedsfall}
				canBeEmpty={false}
			>
				{(path: string, _idx: number) => <DoedsfallForm path={path} />}
			</FormDollyFieldArray>
		</div>
	)
}
