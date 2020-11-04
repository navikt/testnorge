import React from 'react'
import Icon from '~/components/ui/icon/Icon'
import cn from 'classnames'
import './inputWarning.less'

type WarningData = {
	visWarning: boolean
	warningText: string
	children: any
	size?: string
}

export const InputWarning = ({
	visWarning,
	warningText,
	children,
	size = 'small'
}: WarningData) => {
	const css = cn('dolly-form-input', size)

	return (
		<div className="warning-input">
			{children}
			{visWarning && (
				<div className={css}>
					<div className="flexbox">
						<Icon kind="personinformasjon" className="warning-ikon" />
						<div className="warning-text">{warningText}</div>
					</div>
				</div>
			)}
		</div>
	)
}
