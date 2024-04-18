import * as React from 'react'
import { FormDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { initialDoedfoedtBarn } from '@/components/fagsystem/pdlf/form/initialValues'
import { AvansertForm } from '@/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'
import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { UseFormReturn } from 'react-hook-form/dist/types'

interface DoedfoedtBarnProps {
	formMethods: UseFormReturn
	path?: string
}

export const DoedfoedtBarnForm = ({ formMethods, path }: DoedfoedtBarnProps) => {
	return (
		<div className="flexbox--flex-wrap">
			<FormDatepicker name={`${path}.dato`} label="Dødsdato" maxDate={new Date()} />
			<AvansertForm
				path={path}
				kanVelgeMaster={formMethods.watch(`${path}.bekreftelsesdato`) === null}
			/>
		</div>
	)
}

export const DoedfoedtBarn = ({ formMethods }: DoedfoedtBarnProps) => {
	return (
		<FormDollyFieldArray
			name="pdldata.person.doedfoedtBarn"
			header={'Dødfødt barn'}
			newEntry={initialDoedfoedtBarn}
			canBeEmpty={false}
		>
			{(path: string) => <DoedfoedtBarnForm formMethods={formMethods} path={path} />}
		</FormDollyFieldArray>
	)
}
