import { FormDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { getInitialKjoenn } from '@/components/fagsystem/pdlf/form/initialValues'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { AvansertForm } from '@/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { useContext } from 'react'
import {
	BestillingsveilederContext,
	BestillingsveilederContextType,
} from '@/components/bestillingsveileder/BestillingsveilederContext'

type KjoennTypes = {
	path: string
	kanVelgeMaster?: boolean
}

export const KjoennForm = ({ path, kanVelgeMaster }: KjoennTypes) => {
	return (
		<>
			<FormSelect
				name={`${path}.kjoenn`}
				label="Kjønn"
				options={Options('kjoenn')}
				size="large"
				isClearable={false}
			/>
			<AvansertForm path={path} kanVelgeMaster={kanVelgeMaster} />
		</>
	)
}

export const Kjoenn = () => {
	const opts = useContext(BestillingsveilederContext) as BestillingsveilederContextType

	return (
		<div className="flexbox--flex-wrap">
			<FormDollyFieldArray
				name={'pdldata.person.kjoenn'}
				header="Kjønn"
				newEntry={getInitialKjoenn(
					opts?.identMaster === 'PDL' || opts?.identtype === 'NPID' ? 'PDL' : 'FREG',
				)}
				canBeEmpty={false}
			>
				{(path: string) => (
					<KjoennForm
						path={path}
						kanVelgeMaster={opts?.identMaster !== 'PDL' && opts?.identtype !== 'NPID'}
					/>
				)}
			</FormDollyFieldArray>
		</div>
	)
}
