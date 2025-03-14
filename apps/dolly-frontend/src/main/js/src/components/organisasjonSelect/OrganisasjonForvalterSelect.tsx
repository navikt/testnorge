import { DollyTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import Icon from '@/components/ui/icon/Icon'
import Loading from '@/components/ui/loading/Loading'
import React from 'react'

interface OrgProps {
	path: string
	parentPath: string
	success: boolean
	loading?: boolean
	onTextBlur: (event: React.ChangeEvent<any>) => void
}

export const OrganisasjonForvalterSelect = ({
	path,
	parentPath,
	success,
	loading = false,
	onTextBlur,
}: OrgProps) => {
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
