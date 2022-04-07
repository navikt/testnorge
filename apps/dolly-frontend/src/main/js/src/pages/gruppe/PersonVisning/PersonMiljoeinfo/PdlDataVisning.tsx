import React, { useState } from 'react'
import './DataVisning.less'
import 'rc-tooltip/assets/bootstrap_white.css'
import Tooltip from 'rc-tooltip'
import { useBoolean } from 'react-use'
import { DollyApi } from '~/service/Api'
import Icon from '~/components/ui/icon/Icon'
import { PdlVisning } from '~/components/fagsystem/pdl/visning/PdlVisning'

type PdlDataVisningProps = {
	ident: any
}

export const PdlDataVisning = ({ ident }: PdlDataVisningProps) => {
	if (!ident) return null

	const getPersonInfo = () => {
		const [pdlData, setPdlData] = useState(null)
		const [pdlLoading, setPdlLoading] = useBoolean(true)
		const [pdlError, setPdlError] = useState(null)
		if (!pdlData) {
			DollyApi.getPersonFraPdl(ident.ident || ident)
				.then((response) => {
					setPdlData(response.data?.data)
					setPdlLoading(false)
					const feil = response.data?.errors?.find((e) => e.path?.some((i) => i === 'hentPerson'))
					if (feil) {
						setPdlError(feil.message)
					}
				})
				.catch((e) => {
					setPdlLoading(false)
				})
		}
		if (pdlError) {
			return (
				<div className="flexbox--align-center">
					<Icon size={20} kind="report-problem-circle" />
					<div>
						<pre className="api-feilmelding" style={{ fontSize: '1.25em', marginLeft: '5px' }}>
							{pdlError}
						</pre>
					</div>
				</div>
			)
		}
		return <PdlVisning pdlData={pdlData} loading={pdlLoading} />
	}

	return (
		<div className="flexbox--flex-wrap">
			<Tooltip
				overlay={getPersonInfo}
				placement="top"
				align={{
					offset: [0, -10],
				}}
				mouseEnterDelay={0.1}
				mouseLeaveDelay={0.1}
				arrowContent={<div className="rc-tooltip-arrow-inner" />}
				overlayStyle={{ opacity: 1 }}
				destroyTooltipOnHide={{ keepParent: false }}
			>
				<div className="miljoe-knapp">PDL</div>
			</Tooltip>
		</div>
	)
}
