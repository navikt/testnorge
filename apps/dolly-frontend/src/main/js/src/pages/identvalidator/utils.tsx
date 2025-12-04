import { isBoolean } from 'lodash-es'
import { Alert, HStack } from '@navikt/ds-react'
import { oversettBoolean } from '@/utils/DataFormatter'

export const IconComponent = ({ isValid, iconType }) => {
	if (!isBoolean(isValid)) return null
	return iconType === 'none' ? (
		<HStack gap="space-16">
			<div style={{ width: '20px', textAlign: 'center' }}>-</div>
			{oversettBoolean(isValid)}
		</HStack>
	) : (
		<Alert variant={iconType} inline>
			{oversettBoolean(isValid)}
		</Alert>
	)
}

export const getIcon = (isValid: boolean, showError = false) => {
	if (showError) {
		return isValid ? 'success' : 'error'
	}
	return isValid ? 'success' : 'none'
}
