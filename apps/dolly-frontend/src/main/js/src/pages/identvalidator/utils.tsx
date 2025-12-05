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

export const jsonToCsvDownload = (data, filename = 'data.csv') => {
	if (!data || data.length === 0) return

	const headers = Object.keys(data[0])
	const csvRows = [
		headers.join(';'),
		...data.map((row) => headers.map((header) => row[header]).join(';')),
	]

	const csvString = csvRows.join('\n')
	const blob = new Blob([csvString], { type: 'text/csv;charset=utf-8;' })
	const link = document.createElement('a')
	link.href = URL.createObjectURL(blob)
	link.download = filename
	link.target = '_blank'
	document.body.appendChild(link)
	link.click()
	document.body.removeChild(link)
}
