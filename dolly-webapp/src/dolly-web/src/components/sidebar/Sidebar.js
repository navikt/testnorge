import React, { Component } from 'react'
import SidebarLink from './SidebarLink'
import './sidebar.less'

export default class Sidebar extends Component {
	constructor() {
		super()
		this.state = { showItems: [] }
	}

	onClick(index) {
		let showItems = this.state.showItems
		showItems[index] = !showItems[index]
		this.setState({ showItems })
	}

	render() {
		return (
			<div id="sidebar">
				<ul>
					<li onClick={this.onClick.bind(this, 0)}>
						Dolly link1
						{this.state.showItems[0] ? (
							<div>
								<SidebarLink path="/">Submenu1</SidebarLink>
							</div>
						) : null}
					</li>

					<li onClick={this.onClick.bind(this, 1)}>
						Dolly link2
						{this.state.showItems[1] ? (
							<div>
								<SidebarLink path="/">Submenu2</SidebarLink>
							</div>
						) : null}
					</li>

					<li onClick={this.onClick.bind(this, 2)}>
						Dolly link3
						{this.state.showItems[2] ? (
							<div>
								<SidebarLink path="/">Submenu3</SidebarLink>
							</div>
						) : null}
					</li>

					<li onClick={this.onClick.bind(this, 3)}>
						Dolly link4
						{this.state.showItems[3] ? (
							<div>
								<SidebarLink path="/">Submenu4</SidebarLink>
							</div>
						) : null}
					</li>
				</ul>
			</div>
		)
	}
}
