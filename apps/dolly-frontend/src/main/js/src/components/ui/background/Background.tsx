import React from 'react'
import styled from 'styled-components'
// @ts-ignore
import Summer from '~/components/ui/background/backgrounds/Summer.svg'
// @ts-ignore
import Christmas from '~/components/ui/background/backgrounds/Christmas.svg'
// @ts-ignore
import Winter from '~/components/ui/background/backgrounds/Winter.svg'
// @ts-ignore
import Halloween from '~/components/ui/background/backgrounds/Halloween.svg'
// @ts-ignore
import Snowing from '~/components/ui/background/backgrounds/Snowing.svg'
// @ts-ignore
import Fall from '~/components/ui/background/backgrounds/Fall.svg'
// @ts-ignore
import Spring from '~/components/ui/background/backgrounds/Spring.svg'

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

export const WinterBackground = styled.div`
	background-image: url(data:image/svg+xml;base64,${btoa(Winter)});
	background-size: 100% auto;
	background-repeat: no-repeat;
	background-position: bottom;
	background-color: #005077;
`

export const SnowingBackground = styled.div`
	background-image: url(data:image/svg+xml;base64,${btoa(Snowing)});
	background-size: 100% auto;
	background-repeat: no-repeat;
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

export const SpringBackground = styled.div`
	background-image: url(data:image/svg+xml;base64,${btoa(Spring)});
	background-size: 100% auto;
	background-repeat: no-repeat;
	background-position: bottom;
	background-color: #cce4ee;
`
