import Icon from '@/components/ui/icon/Icon'

export const AdminAccessDenied = () => {
	return (
		<div className={'gruppe-feil-tekst'}>
			<Icon size={40} kind="dollyPanic" />
			<p>Du har ikke tilgang til denne siden!</p>
		</div>
	)
}
