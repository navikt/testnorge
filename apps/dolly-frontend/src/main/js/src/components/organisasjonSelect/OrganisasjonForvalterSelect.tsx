import { DollyTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import Icon from '@/components/ui/icon/Icon'
import Loading from '@/components/ui/loading/Loading'
import React from 'react'

interface OrgProps {
	path: string
	value?: any
	success: boolean
	miljoer?: string[]
	loading?: boolean
	onTextBlur: (event: React.ChangeEvent<any>) => void
}

export const OrganisasjonForvalterSelect = ({
	path,
	success,
	miljoer,
	value,
	loading = false,
	onTextBlur,
}: OrgProps) => {
	return (
		<div className={'flexbox--align-start'} style={{ flexFlow: 'column' }} key={path}>
			<DollyTextInput
				name={path}
				fieldName={path}
				value={value}
				type="number"
				size="xlarge"
				label={'Organisasjonsnummer'}
				onBlur={onTextBlur}
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
