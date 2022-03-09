import React from 'react'
import { DollyTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { DollySelect } from '~/components/ui/form/inputs/select/Select'
import Icon from '~/components/ui/icon/Icon'
import Loading from '~/components/ui/loading/Loading'

interface OrgProps {
	path: string
	environment: string
	miljoeOptions: string[]
	error: string
	success: boolean
	loading?: boolean
	onTextBlur: (event: React.ChangeEvent<any>) => void
	onMiljoeChange: (event: any) => void
}

export const OrganisasjonMedMiljoeSelect = ({
	path,
	environment,
	miljoeOptions,
	error,
	success,
	loading = false,
	onTextBlur,
	onMiljoeChange,
}: OrgProps) => {
	const options =
		miljoeOptions &&
		miljoeOptions
			.sort((a, b) =>
				a.localeCompare(b, undefined, {
					numeric: true,
					sensitivity: 'base',
				})
			)
			.map((value) => ({
				value: value,
				label: value.toUpperCase(),
			}))

	return (
		<div className={'flexbox--align-start'}>
			<DollyTextInput
				name={path}
				type="number"
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
				options={options}
				value={environment}
				onChange={onMiljoeChange}
				feil={
					!environment && {
						feilmelding: 'Må velge miljø',
					}
				}
			/>
			{loading && (
				<div className={'flexbox--align-center'} style={{ marginTop: '20px' }}>
					<Loading label="Leter etter organisasjon" />
				</div>
			)}
			{success && !loading && (
				<div className={'flexbox--align-center'} style={{ marginTop: '30px' }}>
					<Icon kind="feedback-check-circle" style={{ marginRight: '5px' }} /> Organisasjon funnet
				</div>
			)}
		</div>
	)
}
