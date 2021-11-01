import React from 'react'
import styled from 'styled-components'
// @ts-ignore
import Summer from '~/pages/loginPage/backgrounds/Summer.svg'
// @ts-ignore
import Christmas from '~/pages/loginPage/backgrounds/Christmas.svg'
// @ts-ignore
import Halloween from '~/pages/loginPage/backgrounds/Halloween.svg'
// @ts-ignore
import Snowing from '~/pages/loginPage/backgrounds/Snowing.svg'
// @ts-ignore
import Fall from '~/pages/loginPage/backgrounds/Fall.svg'

export const SummerBackground = styled.div`
	background-image: url(data:image/svg+xml;base64,${btoa(Summer)});
	background-size: 100% auto;
	background-repeat: no-repeat;
	background-position: bottom;
	background-color: #cce4ee;
`

export const ChristmasBackground = styled.div`
	background-image: url(data:image/svg+xml;base64,${btoa(Christmas)});
	background-size: 100% auto;
	background-repeat: no-repeat;
	background-position: bottom;
	background-color: #005077;
`

export const SnowingBackground = styled.div`
	background-image: url(data:image/svg+xml;base64,${btoa(Snowing)});
	background-size: cover;
	background-position: bottom;
	background-color: #005077;
`

export const HalloweenBackground = styled.div`
	background-image: url(data:image/svg+xml;base64,${btoa(Halloween)});
	background-size: 100% auto;
	background-repeat: no-repeat;
	background-position: bottom;
	background-color: #c0b2d2;
`

export const FallBackground = styled.div`
	background-image: url(data:image/svg+xml;base64,${btoa(Fall)});
	background-size: 100% auto;
	background-repeat: no-repeat;
	background-position: bottom;
	background-color: #cce4ee;
`
