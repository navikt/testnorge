import * as React from 'react'
import { getInitialNyIdent } from '@/components/fagsystem/pdlf/form/initialValues'
import { FormDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { AvansertForm } from '@/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'
import {
	BestillingsveilederContextType,
	useBestillingsveileder,
} from '@/components/bestillingsveileder/BestillingsveilederContext'
import { UseFormReturn } from 'react-hook-form/dist/types'
import { PdlPersonForm } from '@/components/fagsystem/pdlf/form/partials/pdlPerson/PdlPersonForm'

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
					eksisterendeIdent: null as unknown as string,
					kilde: formMethods.watch(`${path}.kilde`),
					master: formMethods.watch(`${path}.master`),
				}

				return (
					<div className="flexbox--flex-wrap">
						<PdlPersonForm
							path={path}
							nyPersonPath={path}
							eksisterendePersonPath={`${path}.eksisterendeIdent`}
							label="NY IDENTITET"
							formMethods={formMethods}
							nyIdentValg={nyIdentValg}
							initialNyIdent={initialNyIdent}
							initialEksisterendePerson={initialEksisterendePerson}
						/>
						<AvansertForm path={path} kanVelgeMaster={opts?.identtype !== 'NPID'} />
					</div>
				)
			}}
		</FormDollyFieldArray>
	)
}
