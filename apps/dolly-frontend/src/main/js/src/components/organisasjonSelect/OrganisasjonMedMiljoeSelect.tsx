import React from 'react'
import { DollyTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { DollySelect } from '~/components/ui/form/inputs/select/Select'
import Icon from '~/components/ui/icon/Icon'
import { FormikProps } from 'formik'

interface OrgProps {
	path: string
	environment: string
	miljoeOptions: any
	error: string
	success: boolean
	onTextBlur: (event: React.ChangeEvent<any>) => void
	onMiljoeChange: (event: React.ChangeEvent<any>) => void
}

export const OrganisasjonMedMiljoeSelect = ({
	path,
	environment,
	miljoeOptions,
	error,
	success,
	onTextBlur,
	onMiljoeChange,
}: OrgProps) => {
	return (
		<div className={'flexbox--align-start'}>
			<DollyTextInput
				name={path}
				type={'number'}
				size="xlarge"
				label={'Organisasjonsnummer'}
				onBlur={onTextBlur}
				feil={
					error && {
						feilmelding: error,
					}
				}
			/>
			<DollySelect
				name={path}
				size={'small'}
				isClearable={false}
				fastfield={false}
				label={'Organisasjon Miljø'}
				options={miljoeOptions}
				value={environment}
				onChange={onMiljoeChange}
				feil={
					!environment && {
						feilmelding: 'Må velge miljø',
					}
				}
			/>
			{success && (
				<div className={'flexbox--align-center'} style={{ marginTop: '30px' }}>
					<Icon kind="feedback-check-circle" style={{ marginRight: '5px' }} /> Organisasjon funnet
				</div>
			)}
		</div>
	)
}
