import React from 'react'
import styled from 'styled-components'
// @ts-ignore
import Summer from './backgrounds/Summer.svg'
// @ts-ignore
import Christmas from './backgrounds/Christmas.svg'
// @ts-ignore
import Halloween from './backgrounds/Halloween.svg'
// @ts-ignore
import Snowing from './backgrounds/Snowing.svg'

export const SummerBackground = styled.div`
	background-image: url(${Summer});
	background-size: 100% auto;
	background-repeat: no-repeat;
	background-position: bottom;
	background-color: #cce4ee;
`

export const ChristmasBackground = styled.div`
	background-image: url(${Christmas});
	background-size: 100% auto;
	background-repeat: no-repeat;
	background-position: bottom;
	background-color: #005077;
`

export const SnowingBackground = styled.div`
	background-image: url(${Snowing});
	background-size: cover;
	background-position: bottom;
	background-color: #005077;
`

export const HalloweenBackground = styled.div`
	background-image: url(${Halloween});
	background-size: 100% auto;
	background-repeat: no-repeat;
	background-position: bottom;
	background-color: #c0b2d2;
`
