import './GruppeFeilmelding.less'
import Icon from '@/components/ui/icon/Icon'

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
			return 'Du mangler tilgang til denne gruppen.'
		case GruppeFeil.FETCH_FAILED:
			return 'Noe gikk galt med henting av gruppe. Ta kontakt med Team Dolly hvis feilen vedvarer.'
		default:
			return 'Noe gikk galt.'
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
