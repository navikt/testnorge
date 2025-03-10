import { DollyTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { DollySelect } from '@/components/ui/form/inputs/select/Select'
import Icon from '@/components/ui/icon/Icon'
import Loading from '@/components/ui/loading/Loading'
import React from 'react'

interface OrgProps {
	path: string
	parentPath: string
	miljoeOptions: string[]
	success: boolean
	loading?: boolean
	onTextBlur: (event: React.ChangeEvent<any>) => void
	onMiljoeChange: (event: any) => void
	formMethods: any
}

export const OrganisasjonMedMiljoeSelect = ({
	path,
	parentPath,
	miljoeOptions,
	success,
	loading = false,
	onTextBlur,
	onMiljoeChange,
	formMethods,
}: OrgProps) => {
	const options =
		miljoeOptions &&
		miljoeOptions?.Q?.map((value: { id: string; label: string }) => ({
			value: value?.id,
			label: value?.label,
		}))

	return (
		<div className={'flexbox--align-start'} key={path}>
			<DollyTextInput
				fieldName={`${parentPath}.opplysningspliktig`}
				name={path}
				type="number"
				size="xlarge"
				label={'Organisasjonsnummer'}
				onBlur={onTextBlur}
			/>
			<DollySelect
				name={`${parentPath}.organisasjonMiljoe`}
				size={'small'}
				isClearable={false}
				label={'Organisasjon miljø'}
				options={options}
				value={formMethods.watch(`${parentPath}.organisasjonMiljoe`)}
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
