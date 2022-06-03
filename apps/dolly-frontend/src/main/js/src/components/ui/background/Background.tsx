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
	background-image: url(data:image/svg+xml;base64,${btoa(Default)});
	background-size: 100% auto;
	background-repeat: no-repeat;
	background-position: bottom;
	background-color: #cce4ee;
`

const ChristmasBackground = styled.div`
	background-image: url(data:image/svg+xml;base64,${btoa(Christmas)});
	background-size: 100% auto;
	background-repeat: no-repeat;
	background-position: bottom;
	background-color: #005077;
`

const WinterBackground = styled.div`
	background-image: url(data:image/svg+xml;base64,${btoa(Winter)});
	background-size: 100% auto;
	background-repeat: no-repeat;
	background-position: bottom;
	background-color: #005077;
`

const HalloweenBackground = styled.div`
	background-image: url(data:image/svg+xml;base64,${btoa(Halloween)});
	background-size: 100% auto;
	background-repeat: no-repeat;
	background-position: bottom;
	background-color: #c0b2d2;
`

const FallBackground = styled.div`
	background-image: url(data:image/svg+xml;base64,${btoa(Fall)});
	background-size: 100% auto;
	background-repeat: no-repeat;
	background-position: bottom;
	background-color: #cce4ee;
`

const SpringBackground = styled.div`
	background-image: url(data:image/svg+xml;base64,${btoa(Spring)});
	background-size: 100% auto;
	background-repeat: no-repeat;
	background-position: bottom;
	background-color: #cce4ee;
`

const PaaskeBackground = styled.div`
	background-image: url(data:image/svg+xml;base64,${btoa(Paaske)});
	background-size: 100% auto;
	background-repeat: no-repeat;
	background-position: bottom;
	background-color: #cce4ee;
`

const SommerBackground = styled.div`
	background-image: url(data:image/svg+xml;base64,${btoa(Sommer)});
	background-size: 100% auto;
	background-repeat: no-repeat;
	background-position: bottom;
	background-color: #cce4ee;
`

export const Background = (props: any) => {
	const month = new Date().getMonth()
	const { beregnetMillimeterRegn = 0 } = useWeatherFyrstikkAlleen()
	if (month >= 2 && month <= 4) {
		return (
			<>
				{Array.from(Array(50).keys()).map((idx) => (
					<div key={idx} className="flower" />
				))}
				<SpringBackground>{props.children}</SpringBackground>
			</>
		)
	} else if (month >= 5 && month <= 7) {
		return (
			<>
				{Array.from(Array(2 * round(beregnetMillimeterRegn) * 10).keys()).map((idx) => (
					<div key={idx} className="rain" />
				))}
				<SommerBackground>{props.children}</SommerBackground>
			</>
		)
	} else if (month >= 8 && month <= 10) {
		return <FallBackground>{props.children}</FallBackground>
	} else if (month === 11 || month === 0 || month === 1) {
		return (
			<>
				{Array.from(Array(70).keys()).map((idx) => (
					<div key={idx} className="snowflake" />
				))}
				<WinterBackground>{props.children}</WinterBackground>
			</>
		)
	}
	return <DefaultBackground>{props.children}</DefaultBackground>
}
