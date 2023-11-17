import { FormikDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { getInitialKjoenn } from '@/components/fagsystem/pdlf/form/initialValues'
import { FormikSelect } from '@/components/ui/form/inputs/select/Select'
import { AvansertForm } from '@/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { useContext } from 'react'
import { BestillingsveilederContext } from '@/components/bestillingsveileder/BestillingsveilederContext'

type KjoennTypes = {
	path: string
	identtype?: string
}

export const KjoennForm = ({ path, identtype }: KjoennTypes) => {
	return (
		<>
			<FormikSelect
				name={`${path}.kjoenn`}
				label="Kjønn"
				options={Options('kjoenn')}
				size="large"
				isClearable={false}
			/>
			<AvansertForm path={path} kanVelgeMaster={identtype !== 'NPID'} />
		</>
	)
}

export const Kjoenn = () => {
	const opts = useContext(BestillingsveilederContext)

	return (
		<div className="flexbox--flex-wrap">
			<FormikDollyFieldArray
				name={'pdldata.person.kjoenn'}
				header="Kjønn"
				newEntry={getInitialKjoenn(opts?.identtype === 'NPID' ? 'PDL' : 'FREG')}
				canBeEmpty={false}
			>
				{(path: string) => <KjoennForm path={path} identtype={opts?.identtype} />}
			</FormikDollyFieldArray>
		</div>
	)
}
