import { DollyTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { DollySelect } from '@/components/ui/form/inputs/select/Select'
import Icon from '@/components/ui/icon/Icon'
import Loading from '@/components/ui/loading/Loading'
import React from 'react'

interface OrgProps {
	path: string
	environment: string
	miljoeOptions: string[]
	success: boolean
	loading?: boolean
	onTextBlur: (event: React.ChangeEvent<any>) => void
	onMiljoeChange: (event: any) => void
}

export const OrganisasjonMedMiljoeSelect = ({
	path,
	environment,
	miljoeOptions,
	success,
	loading = false,
	onTextBlur,
	onMiljoeChange,
}: OrgProps) => {
	const options =
		miljoeOptions &&
		miljoeOptions?.Q?.concat(miljoeOptions?.T)?.map((value: { id: string; label: string }) => ({
			value: value.id,
			label: value.label,
		}))
	const parentPath = path.substring(0, path.lastIndexOf('.'))

	return (
		<div className={'flexbox--align-start'}>
			<DollyTextInput
				fieldName={`${parentPath}.opplysningspliktig`}
				name={path}
				type="number"
				size="xlarge"
				label={'Organisasjonsnummer'}
				onBlur={onTextBlur}
			/>
			<DollySelect
				size={'small'}
				isClearable={false}
				label={'Organisasjon Miljø'}
				options={options}
				value={environment}
				onChange={onMiljoeChange}
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
