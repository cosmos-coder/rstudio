/*
 * NamedPipeProtocol.hpp
 *
 * Copyright (C) 2009-12 by RStudio, Inc.
 *
 * Unless you have received this program directly from RStudio pursuant
 * to the terms of a commercial license agreement with RStudio, then
 * this program is licensed to you under the terms of version 3 of the
 * GNU Affero General Public License. This program is distributed WITHOUT
 * ANY EXPRESS OR IMPLIED WARRANTY, INCLUDING THOSE OF NON-INFRINGEMENT,
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. Please refer to the
 * AGPL (http://www.gnu.org/licenses/agpl-3.0.txt) for more details.
 *
 */

#ifndef CORE_HTTP_NAMED_PIPE_PROTOCOL_HPP
#define CORE_HTTP_NAMED_PIPE_PROTOCOL_HPP

#include <boost/asio/windows/stream_handle.hpp>

#include <core/Error.hpp>

#include <core/http/SocketUtils.hpp>
#include <core/http/NamedPipeAcceptor.hpp>
#include <core/http/AsyncConnectionImpl.hpp>

namespace core {
namespace http {

class NamedPipeProtocol
{
public:
   typedef boost::asio::windows::stream_handle socket;
   typedef NamedPipeAcceptor acceptor;
};

// specialization of closeSocket for stream handle lowest level
template<> Error closeSocket(
           boost::asio::windows::stream_handle::lowest_layer_type& socket);

// specialization of closeSocket for stream handles
template<> Error closeSocket(boost::asio::windows::stream_handle& socket);

// specialization of closeServerSocket
template<> Error closeServerSocket(boost::asio::windows::stream_handle& socket);

// specialization of handleWrite
template<> void AsyncConnectionImpl<NamedPipeProtocol>::handleWrite(
                          const boost::system::error_code& e);


} // namespace http
} // namespace core

#endif // CORE_HTTP_NAMED_PIPE_PROTOCOL_HPP
