import { DollyTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import Icon from '@/components/ui/icon/Icon'
import Loading from '@/components/ui/loading/Loading'
import React from 'react'
import { useFormContext } from 'react-hook-form'

interface OrgProps {
	path: string
	value?: any
	parentPath: string
	success: boolean
	miljoer?: string[]
	loading?: boolean
	error?: Error
	onTextBlur: (event: React.ChangeEvent<any>) => void
}

export const OrganisasjonForvalterSelect = ({
	path,
	parentPath,
	success,
	miljoer,
	value,
	loading = false,
	error,
	onTextBlur,
}: OrgProps) => {
	const { getFieldState } = useFormContext()
	const errorMessage =
		error?.message ||
		getFieldState(path)?.error?.message ||
		getFieldState(parentPath)?.error?.message ||
		getFieldState(`manual.${path}`)?.error?.message ||
		getFieldState(`manual.${parentPath}`)?.error?.message
	return (
		<div className={'flexbox--align-start'} style={{ flexFlow: 'column' }} key={path}>
			<DollyTextInput
				fieldName={path}
				value={value}
				type="number"
				size="xlarge"
				label={'Organisasjonsnummer'}
				onBlur={onTextBlur}
				manualError={errorMessage}
			/>
			{loading && (
				<div className={'flexbox--align-center'}>
					<Loading label="Leter etter organisasjon" />
				</div>
			)}
			{success && !loading && (
				<div className={'flexbox--align-center'}>
					<Icon kind="feedback-check-circle" style={{ marginRight: '5px' }} />
					{miljoer?.length
						? `Organisasjon funnet i miljø: ${miljoer.map((m) => m.toUpperCase()).join(', ')}`
						: 'Organisasjon funnet'}
				</div>
			)}
		</div>
	)
}
