import './GruppeFeilmelding.less'
import Icon from '@/components/ui/icon/Icon'
import {
	ERROR_ACCESS_DENIED,
	ERROR_FETCH_GRUPPE_FAILED,
	ERROR_GENERIC,
} from '../../../ducks/errors/ErrorMessages'

type Props = {
	feil: GruppeFeil
}

export enum GruppeFeil {
	ACCESS_DENIED,
	FETCH_FAILED,
}

export const GruppeFeilmelding = ({ feil }: Props) => {
	return (
		<div className={'gruppe-feil-tekst'}>
			{getIcon(feil)}
			<p>{getFeilmelding(feil)}</p>
		</div>
	)
}

const getFeilmelding = (feil: GruppeFeil) => {
	switch (feil) {
		case GruppeFeil.ACCESS_DENIED:
			return ERROR_ACCESS_DENIED
		case GruppeFeil.FETCH_FAILED:
			return ERROR_FETCH_GRUPPE_FAILED
		default:
			return ERROR_GENERIC
	}
}

const getIcon = (feil: GruppeFeil) => {
	switch (feil) {
		case GruppeFeil.FETCH_FAILED:
			return <Icon size={40} kind="dollyPanic" />
		default:
			return <Icon size={34} kind="report-problem-triangle" />
	}
}
