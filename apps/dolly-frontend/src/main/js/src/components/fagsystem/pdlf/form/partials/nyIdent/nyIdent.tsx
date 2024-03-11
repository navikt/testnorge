import * as React from 'react'
import { useContext } from 'react'
import { getInitialNyIdent } from '@/components/fagsystem/pdlf/form/initialValues'
import { FormDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { AvansertForm } from '@/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'
import { PdlPersonExpander } from '@/components/fagsystem/pdlf/form/partials/pdlPerson/PdlPersonExpander'
import { isEmpty } from '@/components/fagsystem/pdlf/form/partials/utils'
import { BestillingsveilederContext } from '@/components/bestillingsveileder/BestillingsveilederContext'
import { UseFormReturn } from 'react-hook-form/dist/types'

interface NyIdentForm {
	formMethods: UseFormReturn
}

export const NyIdent = ({ formMethods }: NyIdentForm) => {
	const opts = useContext(BestillingsveilederContext)

	return (
		<FormDollyFieldArray
			name="pdldata.person.nyident"
			header="Ny identitet"
			newEntry={getInitialNyIdent(opts?.identtype === 'NPID' ? 'PDL' : 'FREG')}
			canBeEmpty={false}
		>
			{(path: string) => {
				const nyIdentValg = Object.keys(formMethods.watch(path))
					.filter((key) => key !== 'eksisterendeIdent' && key !== 'kilde' && key !== 'master')
					.reduce((obj, key) => {
						obj[key] = formMethods.watch(path)[key]
						return obj
					}, {})

				return (
					<div className="flexbox--flex-wrap">
						<PdlPersonExpander
							nyPersonPath={path}
							eksisterendePersonPath={`${path}.eksisterendeIdent`}
							label="NY IDENTITET"
							formMethods={formMethods}
							nyIdentValg={nyIdentValg}
							isExpanded={
								!isEmpty(nyIdentValg, ['syntetisk']) ||
								formMethods.watch(`${path}.eksisterendeIdent`) !== null
							}
						/>
						<AvansertForm path={path} kanVelgeMaster={opts?.identtype !== 'NPID'} />
					</div>
				)
			}}
		</FormDollyFieldArray>
	)
}
