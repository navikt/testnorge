import cn from 'classnames'
import NavButton from '@/components/ui/button/NavButton/NavButton'

interface BekreftAvbrytKnapper {
	submitknapp: string
	disabled?: boolean
	submitTitle?: string
	onAvbryt: () => void
	onSubmit: () => void
	center?: boolean
}

export default ({
	submitknapp,
	submitTitle,
	disabled,
	onAvbryt,
	onSubmit,
	center,
	...rest
}: BekreftAvbrytKnapper) => {
	const css = cn('dollymodal_buttons', { 'dollymodal_buttons--center': center })

	return (
		<div className={css}>
			<NavButton variant={'danger'} onClick={onAvbryt}>
				Avbryt
			</NavButton>
			<NavButton
				variant={'primary'}
				onClick={onSubmit}
				disabled={disabled}
				title={submitTitle}
				{...rest}
			>
				{submitknapp}
			</NavButton>
		</div>
	)
}
