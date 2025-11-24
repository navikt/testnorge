import * as React from 'react'
import { getInitialNyIdent } from '@/components/fagsystem/pdlf/form/initialValues'
import { FormDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { AvansertForm } from '@/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'
import { PdlPersonExpander } from '@/components/fagsystem/pdlf/form/partials/pdlPerson/PdlPersonExpander'
import { isEmpty } from '@/components/fagsystem/pdlf/form/partials/utils'
import {
	BestillingsveilederContextType,
	useBestillingsveileder,
} from '@/components/bestillingsveileder/BestillingsveilederContext'
import { UseFormReturn } from 'react-hook-form/dist/types'

interface NyIdentForm {
	formMethods: UseFormReturn
}

export const NyIdent = ({ formMethods }: NyIdentForm) => {
	const opts = useBestillingsveileder() as BestillingsveilederContextType

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

				const initialNyIdent = {
					...getInitialNyIdent(),
					kilde: formMethods.watch(`${path}.kilde`),
					master: formMethods.watch(`${path}.master`),
				}

				const initialEksisterendePerson = {
					eksisterendeIdent: '',
					kilde: formMethods.watch(`${path}.kilde`),
					master: formMethods.watch(`${path}.master`),
				}

				return (
					<div className="flexbox--flex-wrap">
						<PdlPersonExpander
							path={path}
							nyPersonPath={path}
							eksisterendePersonPath={`${path}.eksisterendeIdent`}
							initialNyIdent={initialNyIdent}
							initialEksisterendePerson={initialEksisterendePerson}
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
