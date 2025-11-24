import styled from 'styled-components'
// @ts-ignore
import Default from '@/components/ui/background/backgrounds/Default.svg'
// @ts-ignore
import Christmas from '@/components/ui/background/backgrounds/Christmas.svg'
// @ts-ignore
import Winter from '@/components/ui/background/backgrounds/Winter.svg'
// @ts-ignore
import Halloween from '@/components/ui/background/backgrounds/Halloween.svg'
// @ts-ignore
import Fall from '@/components/ui/background/backgrounds/Fall.svg'
// @ts-ignore
import Spring from '@/components/ui/background/backgrounds/Spring.svg'
// @ts-ignore
import Paaske from '@/components/ui/background/backgrounds/Paaske.svg'
// @ts-ignore
import Sommer from '@/components/ui/background/backgrounds/Sommer.svg'
// @ts-ignore
import Faarikaal from '@/components/ui/background/backgrounds/Faarikaal.svg'
import '@/snow.scss'
import '@/rain.scss'
import '@/flowers.scss'
import { NEDBOER_TYPE, useWeatherFyrstikkAlleen } from '@/utils/hooks/useWeather'
import * as _ from 'lodash-es'

const month = new Date().getMonth()
const day = new Date().getDate()
const weekDay = new Date().getDay()

const isHalloween = (month === 9 && day > 14) || (month === 10 && day === 0)
const isSpring = month >= 2 && month <= 4
const isSummer = month >= 5 && month <= 7
const isFall = month >= 8 && month <= 10
const isWinter = month === 0 || month === 1
const isChristmas = month === 11
const isEaster = month === 3 && day > 10 && day < 22
const isFaarikaal = month === 8 && day > 23 && weekDay === 4

const DefaultBackground = styled.div`
	background-image: url(${() => {
		if (isEaster) {
			return Paaske
		} else if (isSpring) {
			return Spring
		} else if (isSummer) {
			return Sommer
		} else if (isHalloween) {
			return Halloween
		} else if (isFaarikaal) {
			return Faarikaal
		} else if (isFall) {
			return Fall
		} else if (isWinter) {
			return Winter
		} else if (isChristmas) {
			return Christmas
		}
		return Default
	}});
	background-size: 100%;
	overflow: hidden;
	background-repeat: no-repeat;
	background-position: center bottom;
	background-color: ${() => {
		if (isHalloween) {
			return '#c0b2d2'
		} else if (isWinter) {
			return '#0C6B99'
		} else if (isChristmas) {
			return '#005077'
		}
		return '#cce4ee'
	}};
`

const animateNedboer = (millimeterNedboer: number, nedBoerType: NEDBOER_TYPE) => {
	const month = new Date().getMonth()
	if (month >= 2 && month <= 4) {
		return (
			<>
				{Array.from(Array(50).keys()).map((idx) => (
					<div key={idx} className="flower" />
				))}
			</>
		)
	} else if (nedBoerType === NEDBOER_TYPE.SNOW) {
		return (
			<>
				{Array.from(Array(70).keys()).map((idx) => (
					<div key={idx} className="snowflake" />
				))}
			</>
		)
	} else {
		return (
			<>
				{Array.from(Array(3 * _.round(millimeterNedboer) * 10).keys()).map((idx) => (
					<div key={idx} className="rain" />
				))}
			</>
		)
	}
}

export const Background = (props: any) => {
	const { millimeterNedboer = 0, nedBoerType } = useWeatherFyrstikkAlleen()
	const nedboer = isChristmas
		? animateNedboer(millimeterNedboer, NEDBOER_TYPE.SNOW)
		: animateNedboer(millimeterNedboer, nedBoerType)
	return (
		<>
			{!isEaster && nedboer}
			<DefaultBackground>{props.children}</DefaultBackground>
		</>
	)
}
