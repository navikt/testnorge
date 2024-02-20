import * as React from 'react'
import { FormikDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { initialDoedfoedtBarn } from '@/components/fagsystem/pdlf/form/initialValues'
import { AvansertForm } from '@/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'
import { FormikDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { DatepickerWrapper } from '@/components/ui/form/inputs/datepicker/DatepickerStyled'
import { UseFormReturn } from 'react-hook-form/dist/types'

interface DoedfoedtBarnProps {
	formMethods: UseFormReturn
	path?: string
}

export const DoedfoedtBarnForm = ({ formMethods, path }: DoedfoedtBarnProps) => {
	return (
		<div className="flexbox--flex-wrap">
			<DatepickerWrapper>
				<FormikDatepicker name={`${path}.dato`} label="Dødsdato" maxDate={new Date()} />
			</DatepickerWrapper>
			<AvansertForm
				path={path}
				kanVelgeMaster={formMethods.watch(`${path}.bekreftelsesdato`) === null}
			/>
		</div>
	)
}

export const DoedfoedtBarn = ({ formMethods }: DoedfoedtBarnProps) => {
	return (
		<FormikDollyFieldArray
			name="pdldata.person.doedfoedtBarn"
			header={'Dødfødt barn'}
			newEntry={initialDoedfoedtBarn}
			canBeEmpty={false}
		>
			{(path: string) => <DoedfoedtBarnForm formMethods={formMethods} path={path} />}
		</FormikDollyFieldArray>
	)
}
