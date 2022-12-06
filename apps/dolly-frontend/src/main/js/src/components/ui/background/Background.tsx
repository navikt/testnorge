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
import { useWeatherFyrstikkAlleen } from '@/utils/hooks/useWeather'
import { round } from 'lodash'

const month = new Date().getMonth()
const day = new Date().getDate()
const weekDay = new Date().getDay()

const isHalloween = (month === 9 && day > 14) || (month === 10 && day === 0)
const isWinter = month === 0 || month === 1
const isChristmas = month === 11

const DefaultBackground = styled.div`
	background-image: url(${() => {
		if (month >= 2 && month <= 4) {
			return Spring
		} else if (month >= 5 && month <= 7) {
			return Sommer
		} else if (isHalloween) {
			return Halloween
		} else if (month === 8 && day > 23 && weekDay === 4) {
			return Faarikaal
		} else if (month >= 8 && month <= 10) {
			return Fall
		} else if (isWinter) {
			return Winter
		} else if (isChristmas) {
			return Christmas
		}
		return Default
	}});
	background-size: 100%;
	background-repeat: no-repeat;
	background-position: center bottom;
	background-color: ${() => {
		if (isHalloween) {
			return '#c0b2d2'
		} else if (isWinter || isChristmas) {
			return '#005077'
		}
		return '#cce4ee'
	}};
`

const PaaskeBackground = styled.div`
	background-image: url(${Paaske});
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
	} else if (month >= 5 && month <= 10) {
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
