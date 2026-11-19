import {
	CheckmarkCircleFillIcon,
	ExclamationmarkTriangleFillIcon,
	InformationSquareFillIcon,
	XMarkOctagonFillIcon,
} from '@navikt/aksel-icons'

type InfostripeVariant = 'info' | 'success' | 'warning' | 'error'

const toInfostripeVariant = (type?: string): InfostripeVariant => {
	const variant = type?.toLowerCase()
	if (variant === 'success' || variant === 'warning' || variant === 'error') {
		return variant
	}
	return 'info'
}

export const getInfostripeColor = (type?: string) => {
	const variant = toInfostripeVariant(type)
	return variant === 'error' ? 'danger' : variant
}

export const getInfostripeStatus = (type?: string) => {
	const variant = toInfostripeVariant(type)
	return variant === 'info' ? 'announcement' : variant
}

interface InfostripeIconProps {
	type?: string
}

export const InfostripeIcon = ({ type }: InfostripeIconProps) => {
	switch (toInfostripeVariant(type)) {
		case 'success':
			return <CheckmarkCircleFillIcon aria-hidden />
		case 'warning':
			return <ExclamationmarkTriangleFillIcon aria-hidden />
		case 'error':
			return <XMarkOctagonFillIcon aria-hidden />
		default:
			return <InformationSquareFillIcon aria-hidden />
	}
}
