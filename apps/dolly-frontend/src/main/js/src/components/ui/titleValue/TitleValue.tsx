import cn from 'classnames'
import * as _ from 'lodash-es'
import KodeverkConnector from '@/components/kodeverk/KodeverkConnector'
import Loading from '@/components/ui/loading/Loading'

import './TitleValue.less'
import { CopyButton } from '@/components/ui/button/CopyButton/CopyButton'
import React from 'react'

const displayValue = (value, visKopier) => {
	return visKopier ? <CopyButton value={value} /> : value
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

	return (
		<div className={className + ' ' + css}>
			{React.createElement(titleType, null, [title])}
			<div>{value ? displayValue(value, visKopier) : children}</div>
		</div>
	)
}

export const TitleValue = ({
	kodeverk = null as unknown as any,
	visKopier = false,
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
	return <P_TitleValue visKopier={visKopier} {...restProps} />
}
