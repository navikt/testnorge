import cn from 'classnames'
import * as _ from 'lodash-es'
import KodeverkConnector from '@/components/kodeverk/KodeverkConnector'
import Loading from '@/components/ui/loading/Loading'

import './TitleValue.less'
import React from 'react'
import { DollyCopyButton } from '@/components/ui/button/CopyButton/DollyCopyButton'
import { isDate } from 'date-fns'
import { formatDate } from '@/utils/DataFormatter'

const displayValue = (value, visKopier) => {
	return visKopier ? (
		<DollyCopyButton
			displayText={value}
			copyText={value}
			tooltipText={'Kopier ' + value}
			style={{ position: 'unset' }}
		/>
	) : (
		value
	)
}

const P_TitleValue = ({
	title,
	value,
	titleType = 'h4',
	size = 'small',
	children,
	className,
	visKopier = false,
}) => {
	const css = cn('title-value', `title-value_${size}`)
	let dispValue = value
	if (isDate(dispValue)) {
		dispValue = formatDate(dispValue)
	}

	return (
		<div className={className + ' ' + css}>
			{React.createElement(titleType, null, [title])}
			<div style={{ hyphens: 'auto' }}>
				{dispValue ? displayValue(dispValue, visKopier) : children}
			</div>
		</div>
	)
}

export const TitleValue = ({
	kodeverk = null as unknown as any,
	visKopier = false,
	hidden = false,
	...restProps
}) => {
	if (!restProps.value && !restProps.children) {
		return null
	}

	if (kodeverk) {
		const { value, children, ...rest } = restProps
		return (
			<P_TitleValue {...rest}>
				<KodeverkConnector navn={kodeverk} value={value}>
					{(kodeverkValues, kodeverkValue) => {
						if (!kodeverkValue)
							return !kodeverkValues ? <Loading /> : displayValue(value, visKopier)
						return _.isFunction(restProps.children)
							? restProps.children(kodeverkValue)
							: kodeverkValue.label
					}}
				</KodeverkConnector>
			</P_TitleValue>
		)
	}
	if (hidden) {
		return
	}
	return <P_TitleValue visKopier={visKopier} {...restProps} />
}
