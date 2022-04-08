import React from 'react'
import BlankHeader from '~/components/layout/blankHeader/BlankHeader'
import BrukerModal from '~/pages/brukerPage/BrukerModel'
import './BrukerPage.less'
import { PaaskeBackground as Background } from '~/components/ui/background/Background'

import './../../snow.scss'

export default () => {
	return (
		<>
			<BlankHeader />
			{Array.from(Array(15).keys()).map(() => (
				<div className="snowflake" />
			))}
			<Background>
				<BrukerModal />
			</Background>
		</>
	)
}
