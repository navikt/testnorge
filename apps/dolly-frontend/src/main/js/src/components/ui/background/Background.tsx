import React from 'react'
import styled from 'styled-components'
// @ts-ignore
import Default from '~/components/ui/background/backgrounds/Default.svg'
// @ts-ignore
import Christmas from '~/components/ui/background/backgrounds/Christmas.svg'
// @ts-ignore
import Winter from '~/components/ui/background/backgrounds/Winter.svg'
// @ts-ignore
import Halloween from '~/components/ui/background/backgrounds/Halloween.svg'
// @ts-ignore
import Fall from '~/components/ui/background/backgrounds/Fall.svg'
// @ts-ignore
import Spring from '~/components/ui/background/backgrounds/Spring.svg'
// @ts-ignore
import Paaske from '~/components/ui/background/backgrounds/Paaske.svg'
// @ts-ignore
import Sommer from '~/components/ui/background/backgrounds/Sommer.svg'
import '~/snow.scss'
import '~/rain.scss'
import '~/flowers.scss'
import { useWeatherFyrstikkAlleen } from '~/utils/hooks/useWeather'
import { round } from 'lodash'

const DefaultBackground = styled.div`
	background-image: url(data:image/svg+xml;base64,${() => {
		const month = new Date().getMonth()
		const day = new Date().getDay()
		if (month >= 2 && month <= 4) {
			return btoa(Spring)
		} else if (month >= 5 && month <= 7) {
			return btoa(Sommer)
		} else if ((month === 9 && day > 14) || (month === 10 && day === 0)) {
			return btoa(Halloween)
		} else if (month >= 8 && month <= 10) {
			return btoa(Fall)
		} else if (month === 0 || month === 1) {
			return btoa(Winter)
		} else if (month === 11) {
			return btoa(Christmas)
		}
		return btoa(Default)
	}});
	background-size: 100%;
	background-repeat: no-repeat;
	background-position: center bottom;
	background-color: rgb(204, 228, 238);
`

const PaaskeBackground = styled.div`
	background-image: url(data:image/svg+xml;base64,${btoa(Paaske)});
	background-size: 100%;
	background-repeat: no-repeat;
	background-position: bottom;
	background-color: #cce4ee;
`

const animateNedboer = (millimeterNedboer: number) => {
	const month = new Date().getMonth()
	if (month >= 2 && month <= 4) {
		return (
			<>
				{Array.from(Array(50).keys()).map((idx) => (
					<div key={idx} className="flower" />
				))}
			</>
		)
	} else if (month >= 5 && month <= 7) {
		return (
			<>
				{Array.from(Array(3 * round(millimeterNedboer) * 10).keys()).map((idx) => (
					<div key={idx} className="rain" />
				))}
			</>
		)
	} else if (month === 11 || month === 0 || month === 1) {
		return (
			<>
				{Array.from(Array(70).keys()).map((idx) => (
					<div key={idx} className="snowflake" />
				))}
			</>
		)
	}
	return null
}

export const Background = (props: any) => {
	const { millimeterNedboer = 0 } = useWeatherFyrstikkAlleen()
	const nedboer = animateNedboer(millimeterNedboer)
	return (
		<>
			{nedboer}
			<DefaultBackground>{props.children}</DefaultBackground>
		</>
	)
}
