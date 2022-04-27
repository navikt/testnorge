import React from 'react'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { AvansertForm } from '~/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { initialDoedsfall } from '~/components/fagsystem/pdlf/form/initialValues'
import styled from 'styled-components'

type DoedsfallTypes = {
	path: string
}

const DatepickerWrapper = styled.div`
	&&&& {
		button {
			position: absolute;
			top: 5px;
			right: 5px;
			margin-right: 0;
		}
	}
`

export const DoedsfallForm = ({ path }: DoedsfallTypes) => {
	return (
		<>
			<DatepickerWrapper>
				<FormikDatepicker name={`${path}.doedsdato`} label="Dødsdato" maxDate={new Date()} />
			</DatepickerWrapper>
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
				{(path: string, idx: number) => <DoedsfallForm path={path} />}
			</FormikDollyFieldArray>
		</div>
	)
}
