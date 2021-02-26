import React from 'react'
import cn from 'classnames'
import _isFunction from 'lodash/isFunction'
import KodeverkConnector from '~/components/kodeverk/KodeverkConnector'
import Loading from '~/components/ui/loading/Loading'

import './TitleValue.less'

const P_TitleValue = ({ title, value, titleType = 'h4', size = 'small', children, className }) => {
	const css = cn('title-value', `title-value_${size}`)

	return (
		<div className={className + ' ' + css}>
			{React.createElement(titleType, null, [title])}
			<div>{value ? value : children}</div>
		</div>
	)
}

export const TitleValue = ({ kodeverk = null, ...restProps }) => {
	if (!restProps.value && !restProps.children) return null

	if (kodeverk) {
		const { value, children, ...rest } = restProps
		return (
			<P_TitleValue {...rest}>
				<KodeverkConnector navn={kodeverk} value={value}>
					{(kodeverkValues, kodeverkValue) => {
						if (!kodeverkValue) return <Loading />
						return _isFunction(restProps.children)
							? restProps.children(kodeverkValue)
							: kodeverkValue.label
					}}
				</KodeverkConnector>
			</P_TitleValue>
		)
	}
	return <P_TitleValue {...restProps} />
}
